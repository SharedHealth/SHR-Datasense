package org.sharedhealth.datasense.handler;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sharedhealth.datasense.BaseIntegrationTest;
import org.sharedhealth.datasense.helpers.DatabaseHelper;
import org.sharedhealth.datasense.helpers.TestConfig;
import org.sharedhealth.datasense.launch.DatabaseConfig;
import org.sharedhealth.datasense.model.Encounter;
import org.sharedhealth.datasense.model.Patient;
import org.sharedhealth.datasense.model.ProcedureRequest;
import org.sharedhealth.datasense.model.fhir.BundleContext;
import org.sharedhealth.datasense.model.fhir.EncounterComposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.sharedhealth.datasense.helpers.ResourceHelper.loadFromXmlFile;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/test-shr-datasense.properties")
@ContextConfiguration(classes = {DatabaseConfig.class, TestConfig.class})
public class ProcedureRequestResourceHandlerIT extends BaseIntegrationTest {
    @Autowired
    private ProcedureRequestResourceHandler procedureRequestResourceHandler;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private EncounterComposition composition;
    private Resource procedureRequest;

    private static final String PATIENT_HID = "98001046534";
    private static final String SHR_ENCOUNTER_ID = "shrEncounterId";

    @Before
    public void setUp() throws Exception {
        super.loadConfigParameters();
    }

    private void setUpData(String fileName, String diagnosticOrderResourceUuid) throws IOException, ParseException {
        Bundle bundle = loadFromXmlFile(fileName);
        BundleContext bundleContext = new BundleContext(bundle, SHR_ENCOUNTER_ID);
        composition = bundleContext.getEncounterCompositions().get(0);
        Patient patient = new Patient();
        patient.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse("2013-12-28"));
        patient.setHid(PATIENT_HID);
        Encounter encounter = new Encounter();
        encounter.setEncounterId(SHR_ENCOUNTER_ID);
        composition.getEncounterReference().setValue(encounter);
        composition.getPatientReference().setValue(patient);
        Reference resourceReference = new Reference().setReference(diagnosticOrderResourceUuid);
        procedureRequest = bundleContext.getResourceForReference(resourceReference);
    }

    @After
    public void tearDown() throws Exception {
        DatabaseHelper.clearDatasenseTables(jdbcTemplate);
    }

    @Test
    public void canHandleDiagnosticOrderResource() throws Exception {
        setUpData("stu3/p98001046534_encounter_with_diagnostic_order_requested.xml",
                "urn:uuid:e8436e26-a011-48e7-a4e8-a41465dfae34#92ad83a5-c835-448d-9401-96554c9a1161");
        assertTrue(procedureRequestResourceHandler.canHandle(procedureRequest));
    }

    @Test
    public void shouldSaveASingleDiagnosticOrder() throws Exception {
        setUpData("stu3/p98001046534_encounter_with_diagnostic_order_requested.xml", "urn:uuid:e8436e26-a011-48e7-a4e8-a41465dfae34#92ad83a5-c835-448d-9401-96554c9a1161");
        procedureRequestResourceHandler.process(procedureRequest, composition);
        List<ProcedureRequest> savedProcedureRequests = findByEncounterId(SHR_ENCOUNTER_ID);
        assertEquals(1, savedProcedureRequests.size());
        ProcedureRequest savedProcedureRequest = savedProcedureRequests.get(0);
        assertDiagnosticOrder(savedProcedureRequest, "BN00ZZZ", "92ad83a5-c835-448d-9401-96554c9a1161", "active",
                "RAD", "01-04-2016", SHR_ENCOUNTER_ID + ":e8436e26-a011-48e7-a4e8-a41465dfae34#92ad83a5-c835-448d-9401-96554c9a1161");
    }

    @Test
    public void shouldDefaultCategoryToLAB() throws Exception {
        setUpData("stu3/p98001046534_encounter_with_diagnostic_order_without_extension.xml", "urn:uuid:e8436e26-a011-48e7-a4e8-a41465dfae34#TR:92ad83a5-c835-448d-9401-96554c9a1161");
        procedureRequestResourceHandler.process(procedureRequest, composition);
        List<ProcedureRequest> savedProcedureRequests = findByEncounterId(SHR_ENCOUNTER_ID);
        assertEquals(1, savedProcedureRequests.size());
        ProcedureRequest savedProcedureRequest = savedProcedureRequests.get(0);
        assertDiagnosticOrder(savedProcedureRequest, "BN00ZZZ", "92ad83a5-c835-448d-9401-96554c9a1161", "active",
                "LAB", "01-04-2016", SHR_ENCOUNTER_ID + ":e8436e26-a011-48e7-a4e8-a41465dfae34#TR:92ad83a5-c835-448d-9401-96554c9a1161");
    }

    @Test
    @Ignore("not valid for stu3")
    public void shouldStoreADiagnosticOrderForEachItemInDiagnosticOrder() throws Exception {
        setUpData("stu3/p98001046534_encounter_with_diagnostic_order_requested_with_multiple_items.xml", "urn:uuid:bc82002c-2cac-4568-b7ed-f73688019b21");
        procedureRequestResourceHandler.process(procedureRequest, composition);
        List<ProcedureRequest> savedProcedureRequests = findByEncounterId(SHR_ENCOUNTER_ID);
        assertEquals(2, savedProcedureRequests.size());
        ProcedureRequest firstOrder = savedProcedureRequests.get(0);
        ProcedureRequest secondOrder = savedProcedureRequests.get(1);
        assertDiagnosticOrder(firstOrder, "Q51.3", "092aa1b8-73f6-11e5-b875-0050568225ca", "requested",
                "LAB", "04-04-2016", SHR_ENCOUNTER_ID + ":bc82002c-2cac-4568-b7ed-f73688019b21");
        assertDiagnosticOrder(secondOrder, "77145-1", "dbf1f2cf-7c9e-11e5-b875-0050568225ca", "requested",
                "LAB", "04-04-2016", SHR_ENCOUNTER_ID + ":bc82002c-2cac-4568-b7ed-f73688019b21");
    }

    @Test
    @Ignore("not valid for stu3")
    public void shouldStoreCancelledDiagnosticOrders() throws Exception {
        setUpData("stu3/p98001046534_encounter_with_diagnostic_order_cancelled_with_multiple_items.xml", "urn:uuid:bc82002c-2cac-4568-b7ed-f73688019b21");
        procedureRequestResourceHandler.process(procedureRequest, composition);
        List<ProcedureRequest> savedProcedureRequests = findByEncounterId(SHR_ENCOUNTER_ID);
        assertEquals(2, savedProcedureRequests.size());
        ProcedureRequest firstOrder = savedProcedureRequests.get(0);
        ProcedureRequest secondOrder = savedProcedureRequests.get(1);
        assertDiagnosticOrder(firstOrder, "Q51.3", "092aa1b8-73f6-11e5-b875-0050568225ca", "cancelled", "LAB",
                "05-04-2016", SHR_ENCOUNTER_ID + ":bc82002c-2cac-4568-b7ed-f73688019b21");
        assertDiagnosticOrder(secondOrder, "77145-1", "dbf1f2cf-7c9e-11e5-b875-0050568225ca", "cancelled", "LAB",
                "05-04-2016", SHR_ENCOUNTER_ID + ":bc82002c-2cac-4568-b7ed-f73688019b21");
    }

    @Test
    public void shouldNotStoreDiagnosticOrderWithoutSystemAndCode() throws Exception {
        setUpData("stu3/p98001046534_encounter_with_diagnostic_order_local.xml", "urn:uuid:4286b394-869f-4b80-be42-0fc3a60f42fe#1");
        procedureRequestResourceHandler.process(procedureRequest, composition);
        List<ProcedureRequest> savedProcedureRequests = findByEncounterId(SHR_ENCOUNTER_ID);
        assertEquals(0, savedProcedureRequests.size());
    }

    private void assertDiagnosticOrder(ProcedureRequest savedProcedureRequest, String orderCode, String orderConcept,
                                       String orderStatus, String orderCategory, String orderDate, String shrOrderUuid) throws ParseException {
        assertEquals(PATIENT_HID, savedProcedureRequest.getPatientHid());
        assertEquals(SHR_ENCOUNTER_ID, savedProcedureRequest.getEncounterId());
        assertEquals(orderCategory, savedProcedureRequest.getOrderCategory());
        assertEquals("24", savedProcedureRequest.getOrderer());
        assertEquals(orderStatus, savedProcedureRequest.getOrderStatus());
        assertEquals(orderCode, savedProcedureRequest.getCode());
        assertEquals(orderConcept, savedProcedureRequest.getOrderConcept());
        assertEquals(shrOrderUuid, savedProcedureRequest.getShrOrderUuid());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        assertEquals(orderDate, simpleDateFormat.format(savedProcedureRequest.getOrderDate()));
        assertNotNull(savedProcedureRequest.getUuid());
    }

    private List<ProcedureRequest> findByEncounterId(String shrEncounterId) {
        String sql = "select patient_hid,encounter_id,order_datetime,order_category,code,orderer," +
                "order_concept,order_status, shr_order_uuid from procedure_request where encounter_id= :encounter_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("encounter_id", shrEncounterId);
        return jdbcTemplate.query(sql, map, new RowMapper<ProcedureRequest>() {
            @Override
            public ProcedureRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
                ProcedureRequest order = new ProcedureRequest();
                order.setPatientHid(rs.getString("patient_hid"));
                order.setEncounterId(rs.getString("encounter_id"));
                order.setOrderDate(rs.getDate("order_datetime"));
                order.setOrderCategory(rs.getString("order_category"));
                order.setcode(rs.getString("code"));
                order.setOrderer(rs.getString("orderer"));
                order.setOrderConcept(rs.getString("order_concept"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setShrOrderUuid(rs.getString("shr_order_uuid"));
                return order;
            }
        });
    }
}