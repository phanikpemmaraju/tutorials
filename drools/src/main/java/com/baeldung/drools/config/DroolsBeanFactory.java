package com.baeldung.drools.config;

import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class DroolsBeanFactory {

    // 1. Create KieServices
    private KieServices kieServices = KieServices.Factory.get();

//    private void getKieRepository() {
//        final KieRepository kieRepository = kieServices.getRepository();
//        kieRepository.addKieModule(() -> kieRepository.getDefaultReleaseId());
//    }

    public KieSession getKieSession(){
        // getKieRepository();

        // 2. Create KieFileSystem and add resources
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        kieFileSystem.write(ResourceFactory.newClassPathResource("com/baeldung/drools/rules/SuggestApplicant.drl"));
        kieFileSystem.write(ResourceFactory.newClassPathResource("com/baeldung/drools/rules/Product_rules.xls"));

        // 3. Create KieBuilder from the file system and run buildAll to create a KieBase.
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();

        // 4. Get KieModule from the builder.
        KieModule kieModule = kb.getKieModule();

        // 5. Create KieContainer from the kieModule's releaseId    .
        KieContainer kContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        // 6. Create new KieSession from the container
        return kContainer.newKieSession();
    }

    public KieSession getKieSession(Resource dt) {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem()
            .write(dt);

        kieServices.newKieBuilder(kieFileSystem)
            .buildAll();

        KieRepository kieRepository = kieServices.getRepository();

        ReleaseId krDefaultReleaseId = kieRepository.getDefaultReleaseId();

        KieContainer kieContainer = kieServices.newKieContainer(krDefaultReleaseId);

        KieSession ksession = kieContainer.newKieSession();

        return ksession;
    }

    /*
     * Can be used for debugging
     * Input excelFile example: com/baeldung/drools/rules/Discount.xls
     */
    public String getDrlFromExcel(String excelFile) {
        DecisionTableConfiguration configuration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        configuration.setInputType(DecisionTableInputType.XLS);

        Resource dt = ResourceFactory.newClassPathResource(excelFile, getClass());
        DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
        String drl = decisionTableProvider.loadFromResource(dt, null);
        return drl;
    }

}
