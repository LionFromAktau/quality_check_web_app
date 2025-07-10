package com.qualityinspection.swequalityinspection.config;

import com.qualityinspection.swequalityinspection.model.entities.*;
import com.qualityinspection.swequalityinspection.model.enums.*;
import com.qualityinspection.swequalityinspection.repository.*;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(2)
public class DatabaseSeeder implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistResultRepository checklistResultRepository;
    private final ChecklistAnswerRepository checklistAnswerRepository;
    private final DefectReportRepository defectReportRepository;
    private final Keycloak keycloak;

    private Map<String, Set<String>> userRolesById = new HashMap<>();

    public DatabaseSeeder(
            ProductRepository productRepository,
            BatchRepository batchRepository,
            ChecklistItemRepository checklistItemRepository,
            ChecklistResultRepository checklistResultRepository,
            ChecklistAnswerRepository checklistAnswerRepository,
            DefectReportRepository defectReportRepository,
            Keycloak keycloak
    ) {
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.checklistResultRepository = checklistResultRepository;
        this.checklistAnswerRepository = checklistAnswerRepository;
        this.defectReportRepository = defectReportRepository;
        this.keycloak = keycloak;
    }

    @Override
    public void run(ApplicationArguments args) {
        fetchAllUserRoles();
        if (userRolesById.isEmpty()) {
            System.out.println("⚠️ No users found in Keycloak. Skipping seeding.");
            return;
        }

        seedProducts();
        seedBatches();
        seedChecklistItems();
        seedChecklistResults();
        seedChecklistAnswers();
        seedDefectReports();
    }

    private void fetchAllUserRoles() {
        List<UserRepresentation> users = keycloak.realm(KeycloakConfig.OAUTH2REALM).users().list();
        for (UserRepresentation user : users) {
            String userId = user.getId();
            if (userId != null && !userId.isEmpty()) {
                List<String> roles = keycloak.realm(KeycloakConfig.OAUTH2REALM).users()
                        .get(userId).roles().realmLevel().listEffective()
                        .stream().map(RoleRepresentation::getName).toList();

                if (!roles.isEmpty()) {
                    userRolesById.put(userId, new HashSet<>(roles));
                }

            }
        }
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        String[] types = {"Type A", "Type B", "Type C", "Type D", "Type E"};

        int productCounter = 1;
        for (String type : types) {
            for (int i = 0; i < 2; i++) {
                ProductEntity p = new ProductEntity();
                p.setName("Product " + productCounter);
                p.setProductType(type);
                productRepository.save(p);
                productCounter++;
            }
        }
    }

    private void seedBatches() {
        if (batchRepository.count() > 0) {
            return;
        }

        List<ProductEntity> products = productRepository.findAll();
        LocalDateTime startDate = LocalDateTime.now().minusDays(180); // старт — 90 дней назад

        for (int i = 1; i <= 180; i++) {
            BatchEntity b = new BatchEntity();
            b.setProduct(products.get(i % products.size()));
            b.setQuantity(100 + i * 5);
            b.setStatus(BatchStatus.CHECKING);
            b.setNotes("Batch " + i + " notes");

            // каждая новая партия на 1 день позже
            LocalDateTime createdDate = startDate.plusDays(i);
            LocalDateTime updatedDate = createdDate.plusHours(2);

            b.setCreatedAt(createdDate);
            b.setUpdatedAt(updatedDate);

            batchRepository.save(b);
        }
    }

    private void seedChecklistItems() {
        if (checklistItemRepository.count() > 0) {
            return;
        }
        List<ProductEntity> products = productRepository.findAll();
        for (int i = 1; i <= 30; i++) {
            ChecklistItemEntity ci = new ChecklistItemEntity();
            ci.setProductEntity(products.get(i % products.size()));
            ci.setDescription("Checklist Item " + i);
            ci.setMandatory(i % 2 == 0);
            checklistItemRepository.save(ci);
        }
    }

    private void seedChecklistResults() {
        if (checklistResultRepository.count() > 0) {
            return;
        }

        List<BatchEntity> batches = new ArrayList<>(batchRepository.findAll());
        if (batches.isEmpty()) {
            System.out.println("⚠️ No batches found. Cannot seed checklist results.");
            return;
        }

        int batchIndex = 0;
        int totalCreated = 0;
        int maxChecklistResults = 110;
        Random random = new Random();

        for (Map.Entry<String, Set<String>> entry : userRolesById.entrySet()) {
            String userId = entry.getKey();
            Set<String> roles = entry.getValue();

            if (roles.contains("role_qualityinspector") || roles.contains("role_productionworker")) {
                for (int month = 1; month <= 12; month++) {
                    int count = 5 + random.nextInt(6); // 5–10

                    for (int i = 0; i < count && batchIndex < batches.size(); i++) {
                        if (totalCreated >= maxChecklistResults) {
                            System.out.println("✅ Reached max checklist results (" + maxChecklistResults + "). Stopping.");
                            return;
                        }

                        ChecklistResultEntity cr = new ChecklistResultEntity();
                        cr.setUser(userId);
                        cr.setBatch(batches.get(batchIndex++)); // уникальный batch

                        cr.setStatus(i % 3 == 0 ? CheckResultStatus.FAIL : CheckResultStatus.SUCCESS);

                        int day = 1 + random.nextInt(25);
                        LocalDateTime created = LocalDateTime.of(LocalDateTime.now().getYear(), month, day, 10, 0);
                        cr.setCreatedAt(created);
                        cr.setUpdatedAt(created.plusMinutes(30));

                        checklistResultRepository.save(cr);
                        totalCreated++;
                    }
                }
            }
        }

        System.out.println("✅ Checklist results created: " + totalCreated);
    }

    private void seedChecklistAnswers() {
        if (checklistAnswerRepository.count() > 0) {
            return;
        }
        List<ChecklistResultEntity> results = checklistResultRepository.findAll();
        if (results.isEmpty()) {
            System.out.println("⚠️ No checklist results found. Skipping checklist answers.");
            return;
        }
        for (int i = 0; i < 110; i++) {
            ChecklistAnswerEntity ca = new ChecklistAnswerEntity();
            ca.setResult(results.get(i % results.size()));
            ca.setItemDescription("Answer to Item " + (i + 1));
            ca.setValue(i % 2 == 0);
            ca.setComment("Comment " + (i + 1));
            ca.setMediaUrl("http://example.com/media/" + (i + 1));
            checklistAnswerRepository.save(ca);
        }
    }

    private void seedDefectReports() {
        if (defectReportRepository.count() > 0) {
            return;
        }

        List<ChecklistResultEntity> allFailedResults = checklistResultRepository.findAll()
                .stream()
                .filter(r -> r.getStatus() == CheckResultStatus.FAIL)
                .collect(Collectors.toList());

        if (allFailedResults.isEmpty()) {
            System.out.println("⚠️ No failed checklist results found. Skipping defect reports.");
            return;
        }

        int resultIndex = 0;
        Random random = new Random();

        for (Map.Entry<String, Set<String>> entry : userRolesById.entrySet()) {
            String userId = entry.getKey();
            Set<String> roles = entry.getValue();

            if (roles.contains("role_qualityinspector")) {
                for (int month = 1; month <= 12; month++) {
                    int count = 5 + random.nextInt(6); // 5–10 дефектов в месяц

                    for (int i = 0; i < count && resultIndex < allFailedResults.size(); i++) {
                        ChecklistResultEntity result = allFailedResults.get(resultIndex++);

                        DefectReportEntity dr = new DefectReportEntity();
                        dr.setChecklistResultId(result);
                        dr.setUserId(userId);
                        dr.setDescription("Defect description " + (month * 10 + i));
                        DefectStatus[] statuses = DefectStatus.values(); // MINOR, MAJOR, CRITICAL, RESOLVED
                        dr.setStatus(statuses[random.nextInt(statuses.length)]);

                        int day = 1 + random.nextInt(25);
                        LocalDateTime created = LocalDateTime.of(LocalDateTime.now().getYear(), month, day, 11, 0);
                        dr.setCreatedAt(created);
                        dr.setUpdatedAt(created.plusHours(1));

                        defectReportRepository.save(dr);
                    }
                }
            }
        }
    }

}
