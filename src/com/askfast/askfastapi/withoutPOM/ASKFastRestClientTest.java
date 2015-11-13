package com.askfast.askfastapi.withoutPOM;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.askfast.askfastapi.AskFastRestClient;
import com.askfast.model.AdapterType;
import com.askfast.model.DDRRecord;
import com.askfast.model.Dialog;
import com.askfast.model.Language;
import com.askfast.model.Recording;
import com.askfast.model.TTSInfo;
import com.askfast.model.TTSProvider;

public class ASKFastRestClientTest {

    // Please add your account info here   
    protected String accountId = "";
    protected String refreshToken = "";
    protected String accessToken = "";
    protected Logger LOG = Logger.getLogger(ASKFastRestClientTest.class.getSimpleName());
    
    @Before
    public void setup() {
        
        if(accessToken==null) {
            try {
                AskFastRestClient client = new AskFastRestClient(accountId, refreshToken);
                accessToken = client.getAccessToken();
            } catch (Exception e) {
                LOG.severe("Failed to get accessToken: "+e.getMessage());
            }
        }
    }
    
    @Test
    public void testCreatingDialog()
    {
        AskFastRestClient client = new AskFastRestClient(accountId, refreshToken, accessToken);
        
        // Check Dialog count
        Set<Dialog> dialogs = client.getDialogs();
        int count = dialogs.size();
        LOG.info("Found "+count+" dialogs");
        Assert.assertTrue(count > 0);
        
        String name = "Test Dialog";
        String url = "http://test.me/";
        
        // Create dialog
        Dialog newDialog = client.createDialog(new Dialog(name, url));
        String dialogId = newDialog.getId();        
        
        // Check values of new dialog
        Dialog dialog = client.getDialog(dialogId); 
        Assert.assertEquals(name, dialog.getName());
        Assert.assertEquals(url, dialog.getUrl());
        Assert.assertNull( dialog.getTtsInfo() );
        Assert.assertFalse( dialog.isUseBasicAuth() );
        
        // Check Dialog count
        dialogs = client.getDialogs();
        LOG.info("Found "+dialogs.size()+" dialogs");
        Assert.assertTrue(dialogs.size() == count + 1);
        
        // Update Dialog the name and url
        String newName = "Test Dialog 2";
        String newUrl = "http://test.me/2";
        Dialog updatedDialog = client.updateDialog(dialogId, new Dialog(newName, newUrl));
        Assert.assertEquals(newName, updatedDialog.getName());
        Assert.assertEquals(newUrl, updatedDialog.getUrl());
        
        // Check new values of new dialog
        dialog = client.getDialog(dialogId); 
        Assert.assertEquals(newName, dialog.getName());
        Assert.assertEquals(newUrl, dialog.getUrl());
        
        // Update the tss info
        TTSInfo ttsInfo = new TTSInfo(TTSProvider.ACAPELA, Language.ENGLISH_GREATBRITAIN, "sharon8k");
        dialog.setTtsInfo( ttsInfo );
        updatedDialog = client.updateDialog( dialog.getId(), dialog );
        Assert.assertNotNull( updatedDialog.getTtsInfo() );
        Assert.assertEquals( TTSProvider.ACAPELA, updatedDialog.getTtsInfo().getProvider() );
        Assert.assertEquals( Language.ENGLISH_GREATBRITAIN, updatedDialog.getTtsInfo().getLanguage() );
        Assert.assertEquals( "sharon8k", updatedDialog.getTtsInfo().getVoiceUsed() );
        Assert.assertFalse( updatedDialog.isUseBasicAuth() );
        
        // Remove dialog
        client.removeDialog(dialogId);
        
        // Check Dialog count
        dialogs = client.getDialogs();
        LOG.info("Found "+dialogs.size()+" dialogs");
        Assert.assertTrue(dialogs.size() == count);
    }
    
    @Test
    public void testReadingDDRRecords() throws Exception {

        if (!isNullOrEmpty(accountId) && !isNullOrEmpty(accessToken) && !isNullOrEmpty(refreshToken)) {
            AskFastRestClient client = new AskFastRestClient(accountId, refreshToken, accessToken);
            List<DDRRecord> ddrs = client.getDDRRecords(null, null, null, null, null, null, null, null, null, null, null,
                                                        null);

            LOG.info("Found " + ddrs.size() + " ddrs");
            Assert.assertTrue(ddrs.size() > 0);

            ddrs = client.getDDRRecords(null, null, null, null, null, System.currentTimeMillis(), null, null, null,
                null, null, null);
            Assert.assertTrue(ddrs.size() == 0);
        }
    }
    
    @Test
    public void testReadingRecordings() {
        AskFastRestClient client = new AskFastRestClient(accountId, refreshToken, accessToken);
        List<Recording> recordings = client.getRecordings();
        
        int count = recordings.size();
        LOG.info("Found " + count + " recordings");
        
        Assert.assertTrue(count > 0);
    }
    
    /**
     * This is a test to fetch the number of ddrRecords using the rest endpoint of ASK-FAST api
     * @throws Exception 
     */
    @Test
    public void fetchDDRCountTest() throws Exception {

        if (!isNullOrEmpty(accountId) && !isNullOrEmpty(accessToken) && !isNullOrEmpty(refreshToken)) {
            AskFastRestClient client = new AskFastRestClient(accountId, refreshToken, accessToken);
            Integer ddrRecordCount = client.getDDRRecordCount(null,
                Arrays.asList(AdapterType.SMS.toString(), AdapterType.CALL.toString()), null, null, null, null, null,
                null, null);
            Assert.assertTrue(ddrRecordCount > 0);
        }
    }
    
    private boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
