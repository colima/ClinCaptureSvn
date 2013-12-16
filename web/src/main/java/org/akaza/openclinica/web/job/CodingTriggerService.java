package org.akaza.openclinica.web.job;


import com.clinovo.model.CodedItem;
import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.util.Date;

public class CodingTriggerService {

    public CodingTriggerService() {

    }

    public static final String CODED_ITEM_ID = "codedItem";
    public static final String VERBATIUM_TERM = "verbatimTerm";
    public static final String IS_ALIAS = "isAlias";


    public SimpleTriggerImpl generateCodeItemService(String codedItemId, String verbatiumTerm, boolean isAlias) {

        JobDataMap codeJobDataMap = new JobDataMap();
        codeJobDataMap.put(CODED_ITEM_ID, codedItemId);
        codeJobDataMap.put(VERBATIUM_TERM, verbatiumTerm);
        codeJobDataMap.put(IS_ALIAS, String.valueOf(isAlias));
        SimpleTriggerImpl sTrigger = new SimpleTriggerImpl();
        sTrigger.setName("coding_item_" + codedItemId + " verbatiumTerm_" + verbatiumTerm + " " + System.currentTimeMillis());
        sTrigger.setGroup("CODING");
        sTrigger.setRepeatCount(0);
        sTrigger.setRepeatInterval(1);
        sTrigger.setStartTime(new Date());
        sTrigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        sTrigger.setJobDataMap(codeJobDataMap);
        return sTrigger;
    }
}