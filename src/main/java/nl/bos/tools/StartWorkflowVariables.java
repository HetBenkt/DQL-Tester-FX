package nl.bos.tools;

import com.documentum.bpm.IDfWorkflowEx;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

public class StartWorkflowVariables extends StartWorkflow {

    private StartWorkflowVariables(String repository, String username, String password) {
        super(repository, username, password);
    }

    public static void main(String[] args) {
        new StartWorkflowVariables(args[0], args[1], args[2]);
    }

    @Override
    protected IDfId startWorkflow() throws DfException {
        IDfWorkflowBuilder wfBldrObj = session.newWorkflowBuilder(new DfId("4b12d591800015e0")); //dm_process.r_object_id
        IDfId initWorkflow = wfBldrObj.initWorkflow();

        IDfWorkflowEx process = (IDfWorkflowEx) session.getObject(initWorkflow);
        process.setPrimitiveObjectValue("StringVar", "HelloWorld");
        process.setPrimitiveObjectValue("IntegerVar", 10);
        process.setPrimitiveObjectValue("FloatVar", 2.4);
        process.setPrimitiveObjectValue("BooleanVar", true);
        process.setPrimitiveObjectValue("DateVar", "??");
        process.setStructuredDataTypeAttrValue("user", "username", "testuser");

        return wfBldrObj.runWorkflow();
    }
}
