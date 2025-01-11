package com.downtown8.cordova.plugin;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.pax.poslink.BatchRequest;
import com.pax.poslink.CommSetting;
import com.pax.poslink.ManageRequest;
import com.pax.poslink.POSLinkAndroid;
import com.pax.poslink.PaymentRequest;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.ProcessTransResult.ProcessTransResultCode;
import com.pax.poslink.ReportRequest;
import com.pax.poslink.peripheries.POSLinkPrinter;
import com.pax.poslink.peripheries.POSLinkScanner;
import com.pax.poslink.peripheries.ProcessResult;
import com.pax.poslink.poslink.POSLinkCreator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonathan Zhang on 19/07/09.
 */

public class PAXPos extends CordovaPlugin {
    private static final String TAG = "PAXPos"; // log label

    private Context mContext = null;

    private POSLinkScanner posLinkScanner;
    private PosLink posLink;
    private POSLinkPrinter posLinkPrinter;
    private String currentScannerType;
    private StringBuffer scanResult = new StringBuffer();


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.mContext = cordova.getActivity().getApplicationContext();
        CommSetting commSetting = new CommSetting();
        if (Build.MODEL.startsWith("E")) {
            commSetting.setType(CommSetting.USB);
        } else if (Build.MODEL.startsWith("A9") || Build.MODEL.startsWith("A6")){
            commSetting.setType(CommSetting.AIDL);
        } else {
            commSetting.setType(CommSetting.TCP);
        }
        /*
        commSetting.setTimeOut("60000");
        commSetting.setSerialPort("COM1");
        commSetting.setBaudRate("9600");
        commSetting.setDestIP("172.16.20.15");
        commSetting.setDestPort("10009");
        commSetting.setMacAddr("");
        commSetting.setEnableProxy(false);
        */
        POSLinkAndroid.init(this.mContext, commSetting);
        posLink = POSLinkCreator.createPoslink(this.mContext);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if(mContext != null) {
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        Log.d(TAG, "onPause");
        if(mContext != null) {
        }
    }

    private boolean executePrint(byte[] data, final CallbackContext callbackContext) {
        POSLinkPrinter posLinkPrinter = POSLinkPrinter.getInstance(mContext);
        posLinkPrinter.print(new String(data),  new POSLinkPrinter.PrintListener() {
            @Override
            public void onSuccess() {
                callbackContext.success();
            }

            @Override
            public void onError(ProcessResult processResult) {
                JSONObject errorObj = new JSONObject();
                try {
                    errorObj.put("code", processResult.getCode());
                    errorObj.put("message", processResult.getMessage());
                }catch(JSONException ex){
                    LOG.e(TAG, "executeOpenScan", ex);
                }
                callbackContext.error(errorObj);
            }
        });
        return true;
    }

    private boolean executeOpenScan(String scannerType, CallbackContext callbackContext) {
        if(scannerType == null) scannerType = POSLinkScanner.ScannerType.REAR;
        if(posLinkScanner != null){
            if(scannerType.equals(currentScannerType)){
                callbackContext.success();
                return true;
            }else{
                ProcessResult result = posLinkScanner.close();
                if (!result.getCode().equals(ProcessResult.CODE_OK)) {
                    JSONObject errorObj = new JSONObject();
                    try {
                        errorObj.put("code", result.getCode());
                        errorObj.put("message", result.getMessage());
                    }catch(JSONException ex){
                        LOG.e(TAG, "executeOpenScan", ex);
                    }
                    callbackContext.error(errorObj);
                    return true;
                }
                posLinkScanner = null;
            }
        }
        posLinkScanner = POSLinkScanner.getPOSLinkScanner(mContext, scannerType);
        ProcessResult result = posLinkScanner.open();
        if (!result.getCode().equals(ProcessResult.CODE_OK)) {
            JSONObject errorObj = new JSONObject();
            try {
                errorObj.put("code", result.getCode());
                errorObj.put("message", result.getMessage());
            }catch(JSONException ex){
                LOG.e(TAG, "executeOpenScan", ex);
            }
            callbackContext.error(errorObj);
        }else{
            currentScannerType = scannerType;
            callbackContext.success();
        }

        return true;
    }

    private boolean executeCloseScan(CallbackContext callbackContext) {
        if(posLinkScanner == null){
            callbackContext.success();
            return true;
        }
        ProcessResult result = posLinkScanner.close();
        if (!result.getCode().equals(ProcessResult.CODE_OK)) {
            JSONObject errorObj = new JSONObject();
            try {
                errorObj.put("code", result.getCode());
                errorObj.put("message", result.getMessage());
            }catch(JSONException ex){
                LOG.e(TAG, "executeOpenScan", ex);
            }
            callbackContext.error(errorObj);
        }else {
            callbackContext.success();
        }
        return true;
    }

    private boolean executeScan(final CallbackContext callbackContext) {
        scanResult.setLength(0);
        posLinkScanner.start(new POSLinkScanner.ScannerListener() {
            @Override
            public void onRead(String s) {
                scanResult.append(s);
            }

            @Override
            public void onFinish() {
                callbackContext.success(scanResult.toString());
            }
        });
        return true;
    }

    private void runProcessTrans(final String type, final CallbackContext callbackContext){
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                ProcessTransResult ptr = posLink.ProcessTrans();
                JSONObject resultobj = new JSONObject();
                if (ptr.Code == ProcessTransResultCode.OK) {
                    Object response = null;
                    if("payment".equals(type)) {
                        response = posLink.PaymentResponse;
                    }else if("batch".equals(type)){
                        response = posLink.BatchResponse;
                    }else if("manage".equals(type)){
                        response = posLink.ManageResponse;
                    }else if("report".equals(type)){
                        response = posLink.ReportResponse;
                    }
                    try {
                        resultobj.put("status", "success");
                        resultobj.put("response", response);
                        callbackContext.success(resultobj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackContext.error("JSON Exception");
                    }
                    Log.i(TAG, "Transaction succeed!");

                } else if (ptr.Code == ProcessTransResultCode.TimeOut) {

                    try {
                        resultobj.put("status", "timeout");
                        resultobj.put("code", String.valueOf(ptr.Code));
                        resultobj.put("message", ptr.Msg);
                        callbackContext.success(resultobj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackContext.error("JSON Exception");
                    }
                    Log.e(TAG, "Transaction TimeOut! " + String.valueOf(ptr.Code));
                    Log.e(TAG, "Transaction TimeOut! " + ptr.Msg);

                } else {

                    try {
                        resultobj.put("status", "error");
                        resultobj.put("code", String.valueOf(ptr.Code));
                        resultobj.put("message", ptr.Msg);
                        callbackContext.success(resultobj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackContext.error("JSON Exception");
                    }

                    Log.e(TAG, "Transaction Error! " + String.valueOf(ptr.Code));
                    Log.e(TAG, "Transaction Error! " + ptr.Msg);
                }
            }
        });
    }

    private void setPaymentRequest(PaymentRequest request, JSONObject requestBody) {
        try {
            request.TenderType = request.ParseTenderType(requestBody.getString("tenderType"));
            request.TransType = request.ParseTransType(requestBody.getString("transType"));
            request.ExtData = requestBody.getString("extData");
            request.Amount = requestBody.getString("amount");
            request.CashBackAmt = requestBody.getString("cashBackAmt");
            request.ClerkID = requestBody.getString("clerkID");
            request.Zip = requestBody.getString("zip");
            request.TipAmt = requestBody.getString("tipAmt");
            request.TaxAmt = requestBody.getString("taxAmt");
            request.FuelAmt = requestBody.getString("fuelAmt");
            request.Street = requestBody.getString("street");
            request.Street2 = requestBody.getString("street2");
            request.SurchargeAmt = requestBody.getString("surchargeAmt");
            request.PONum = requestBody.getString("poNum");
            request.OrigRefNum = requestBody.getString("origRefNum");
            request.InvNum = requestBody.getString("invNum");
            request.ECRRefNum = requestBody.getString("ecrRefNum");
            request.OrigECRRefNum = requestBody.getString("origECRRefNum");
            request.ECRTransID = requestBody.getString("ecrTransID");
            request.AuthCode = requestBody.getString("authCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean executePayment(JSONObject requestBody, final CallbackContext callbackContext) {
        PaymentRequest payrequest = new PaymentRequest();
        setPaymentRequest(payrequest, requestBody);
        posLink.PaymentRequest = payrequest;
        runProcessTrans("payment", callbackContext);
        return true;
    }

    private boolean executeBatch(JSONObject requestBody, final CallbackContext callbackContext) {
        BatchRequest batchrequest = new BatchRequest();
        setBatchRequest(batchrequest, requestBody);
        Log.i(TAG, "BatchRequest.TransType = " + batchrequest.TransType);
        posLink.BatchRequest = batchrequest;
        runProcessTrans("batch", callbackContext);
        return true;
    }

    private void setBatchRequest(BatchRequest request, JSONObject requestBody) {
        try {
            request.TransType = request.ParseTransType(requestBody.getString("transType"));
            request.EDCType = request.ParseEDCType(requestBody.getString("edcType"));
            request.ExtData = requestBody.getString("extData");
            request.Timestamp = requestBody.getString("timestamp");
            request.SAFIndicator = requestBody.getString("safIndicator");
            request.PaymentType = request.ParsePaymentType(requestBody.getString("paymentType"));
            request.CardType = request.ParseCardType(requestBody.getString("cardType"));
            request.RecordNum = requestBody.getString("recordNum");
            request.RefNum = requestBody.getString("refNum");
            request.AuthCode = requestBody.getString("authCode");
            request.ECRRefNum = requestBody.getString("ecrRefNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean executeManage(JSONObject requestBody, final CallbackContext callbackContext) {
        ManageRequest mgrequest = new ManageRequest();
        setManageRequest(mgrequest, requestBody);
        Log.i(TAG, "ManageRequest.TransType = " + mgrequest.TransType);
        posLink.ManageRequest = mgrequest;
        runProcessTrans("manage", callbackContext);
        return true;
    }

    private void setManageRequest(ManageRequest request, JSONObject requestBody) {
        try {
            request.TransType = request.ParseTransType(requestBody.getString("transType"));
            request.EDCType = request.ParseEDCType(requestBody.getString("edcType"));
            request.Trans = request.ParseTrans(requestBody.getString("trans"));
            request.ExtData = requestBody.getString("extData");
            request.VarName = requestBody.getString("varName");
            request.VarName1 = requestBody.getString("varName1");
            request.VarName2 = requestBody.getString("varName2");
            request.VarName3 = requestBody.getString("varName3");
            request.VarName4 = requestBody.getString("varName4");
            request.VarValue1 = requestBody.getString("varValue1");
            request.VarValue2 = requestBody.getString("varValue2");
            request.VarValue3 = requestBody.getString("varValue3");
            request.VarValue4 = requestBody.getString("varValue4");
            request.VarValue = requestBody.getString("varValue");
            request.Title = requestBody.getString("title");
            request.ThankYouTitle = requestBody.getString("thankYouTitle");
            request.Button1 = requestBody.getString("button1");
            request.Button2 = requestBody.getString("button2");
            request.Button3 = requestBody.getString("button3");
            request.Button4 = requestBody.getString("button4");
            request.SigSavePath = requestBody.getString("sigSavePath");
            request.TimeOut = requestBody.getString("timeOut");
            request.ThankYouTimeOut = requestBody.getString("thankYouTimeOut");
            request.DisplayMessage = requestBody.getString("displayMessage");
            request.DisplayMessage2 = requestBody.getString("displayMessage2");
            request.ImagePath = requestBody.getString("imagePath");
            if (!TextUtils.isEmpty(requestBody.getString("upload")) && TextUtils.isDigitsOnly(requestBody.getString("upload"))) {
                request.Upload = Integer.parseInt(requestBody.getString("upload"));
            }
            request.HRefNum = requestBody.getString("hRefNum");
            request.ImageName = requestBody.getString("imageName");
            request.ImageDescription = requestBody.getString("imageDescription");
            request.ThankYouMessage1 = requestBody.getString("thankYouMessage1");
            request.ThankYouMessage2 = requestBody.getString("thankYouMessage2");
            request.AccountNumber = requestBody.getString("accountNumber");
            request.EncryptionType = requestBody.getString("encryptionType");
            request.KeySlot = requestBody.getString("keySlot");
            request.PinMinLength = requestBody.getString("pinMinLength");
            request.MagneticSwipeEntryFlag = requestBody.getString("magneticSwipeEntryFlag");
            request.ManualEntryFlag = requestBody.getString("manualEntryFlag");
            request.ContactlessEntryFlag = requestBody.getString("contactlessEntryFlag");
            request.ScannerEntryFlag = requestBody.getString("scannerEntryFlag");
            request.ExpiryDatePrompt = requestBody.getString("expiryDatePrompt");
            request.EncryptionFlag = requestBody.getString("encryptionFlag");
            request.ContactEMVEntryFlag = requestBody.getString("contactEMVEntryFlag");
            request.FallbackSwipeEntryFlag = requestBody.getString("fallbackSwipeEntryFlag");
            request.MINAccountLength = requestBody.getString("minAccountLength");
            request.MAXAccountLength = requestBody.getString("maxAccountLength");
            request.InputType = requestBody.getString("inputType");
            request.MINLength = requestBody.getString("minLength");
            request.MAXLength = requestBody.getString("maxLength");
            request.DefaultValue = requestBody.getString("defaultValue");
            request.FileName = requestBody.getString("fileName");
            request.Amount = requestBody.getString("amount");
            request.TipAmt = requestBody.getString("tipAmt");
            request.CashBackAmt = requestBody.getString("cashBackAmt");
            request.SurchargeAmt = requestBody.getString("surchargeAmt");
            request.TaxAmt = requestBody.getString("taxAmt");
            request.MerchantDecision = requestBody.getString("merchantDecision");
            request.PinMaxLength = requestBody.getString("pinMaxLength");
            request.NullPinFlag = requestBody.getString("nullPinFlag");
            request.PinAlgorithm = requestBody.getString("pinAlgorithm");
            request.CurrencyCode = requestBody.getString("currencyCode");
            request.CountryCode = requestBody.getString("countryCode");
            request.OnlineAuthorizationResult = requestBody.getString("onlineAuthorizationResult");
            request.ResponseCode = requestBody.getString("responseCode");
            request.AuthorizationCode = requestBody.getString("authorizationCode");
            request.IssuerScript1 = requestBody.getString("issuerScript1");
            request.IssuerScript2 = requestBody.getString("issuerScript2");
            request.Message1 = requestBody.getString("message1");
            request.Message2 = requestBody.getString("message2");
            request.TagList = requestBody.getString("tagList");
            request.TLVType = requestBody.getString("tlvType");
            request.EMVData = requestBody.getString("emvData");
            request.IssuerAuthenticationData = requestBody.getString("issuerAuthenticationData");
            request.SAFMode = requestBody.getString("safMode");
            request.StartDateTime = requestBody.getString("startDateTime");
            request.EndDateTime = requestBody.getString("endDateTime");
            request.DurationInDays = requestBody.getString("durationInDays");
            request.MaxNumber = requestBody.getString("maxNumber");
            request.TotalCeilingAmount = requestBody.getString("totalCeilingAmount");
            request.CeilingAmountPerCardType = requestBody.getString("ceilingAmountPerCardType");
            request.HALOPerCardType = requestBody.getString("haloPerCardType");
            request.SAFUploadMode = requestBody.getString("safUploadMode");
            request.AutoUploadIntervalTime = requestBody.getString("autoUploadIntervalTime");
            request.DeleteSAFConfirmation = requestBody.getString("deleteSAFConfirmation");
            request.Text = requestBody.getString("text");
            request.ButtonColor1 = requestBody.getString("buttonColor1");
            request.ButtonColor2 = requestBody.getString("buttonColor2");
            request.ButtonColor3 = requestBody.getString("buttonColor3");
            request.ButtonKey1 = requestBody.getString("buttonKey1");
            request.ButtonKey2 = requestBody.getString("buttonKey2");
            request.ButtonKey3 = requestBody.getString("buttonKey3");
            request.EnableHardKey = requestBody.getString("enableHardKey");
            request.HardKeyList = requestBody.getString("hardKeyList");
            request.LastReceipt = requestBody.getString("lastReceipt");
            request.RefNum = requestBody.getString("refNum");
            request.ECRRefNum = requestBody.getString("ecrRefNum");
            request.PrintCopy = requestBody.getString("printCopy");
            request.PrintData = requestBody.getString("printData");
            request.TopDown = requestBody.getString("topDown");
            request.TaxLine = requestBody.getString("taxLine");
            request.TotalLine = requestBody.getString("totalLine");
            request.ItemData = requestBody.getString("itemData");
            request.EmvKernelConfigurationSelection = requestBody.getString("emvKernelConfigurationSelection");
            request.TransactionDate = requestBody.getString("transactionDate");
            request.TransactionTime = requestBody.getString("transactionTime");
            request.CurrencyExponent = requestBody.getString("currencyExponent");
            request.CVVPrompt = requestBody.getString("cvvPrompt");
            request.ZipCodePrompt = requestBody.getString("zipCodePrompt");
            request.MerchantCategoryCode = requestBody.getString("merchantCategoryCode");
            request.Label1 = requestBody.getString("label1");
            request.Label2 = requestBody.getString("label2");
            request.Label3 = requestBody.getString("label3");
            request.Label4 = requestBody.getString("label4");
            request.Label1Property = requestBody.getString("label1Property");
            request.Label2Property = requestBody.getString("label2Property");
            request.Label3Property = requestBody.getString("label3Property");
            request.Label4Property = requestBody.getString("label4Property");
            request.SignatureBox = requestBody.getString("signatureBox");
            request.ButtonType = requestBody.getString("buttonType");
            request.PaddingChar = requestBody.getString("paddingChar");
            request.TrackDataSentinel = requestBody.getString("trackDataSentinel");
            request.CameraScanReader = requestBody.getString("cameraScanReader");
            request.TokenCommand = requestBody.getString("tokenCommand");
            request.Token = requestBody.getString("token");
            request.TokenSN = requestBody.getString("tokenSN");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean executeReport(JSONObject requestBody, final CallbackContext callbackContext) {
        ReportRequest reprequest = new ReportRequest();
        setReportRequest(reprequest, requestBody);
        Log.i(TAG, "ReportRequest.TransType = " + reprequest.TransType);
        posLink.ReportRequest = reprequest;
        runProcessTrans("report", callbackContext);
        return true;
    }

    private void setReportRequest(ReportRequest request, JSONObject requestBody) {
        try {
            request.TransType = request.ParseTransType(requestBody.getString("transType"));
            request.EDCType = request.ParseEDCType(requestBody.getString("edcType"));
            request.ExtData = requestBody.getString("extData");
            request.SAFIndicator = requestBody.getString("safIndicator");
            request.PaymentType = request.ParsePaymentType(requestBody.getString("paymentType"));
            request.CardType = request.ParseCardType(requestBody.getString("cardType"));
            request.RecordNum = requestBody.getString("recordNum");
            request.RefNum = requestBody.getString("refNum");
            request.AuthCode = requestBody.getString("authCode");
            request.ECRRefNum = requestBody.getString("ecrRefNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute action:" + action + "|||args:" + args);
        if(action.equals("openScan")){
            String scannerType = args.getString(0);
            return executeOpenScan(scannerType, callbackContext);
        }else if(action.equals("closeScan")){
            return executeCloseScan(callbackContext);
        }else if(action.equals("scan")){
            return executeScan(callbackContext);
        }else if(action.equals("processPayment")){
            JSONObject requestBody = args.getJSONObject(0);
            return executePayment(requestBody, callbackContext);
        }else if(action.equals("processReport")){
            JSONObject requestBody = args.getJSONObject(0);
            return executeReport(requestBody, callbackContext);
        }else if(action.equals("processManage")){
            JSONObject requestBody = args.getJSONObject(0);
            return executeManage(requestBody, callbackContext);
        }else if(action.equals("processBatch")){
            JSONObject requestBody = args.getJSONObject(0);
            return executeBatch(requestBody, callbackContext);
        }else if(action.equals("executePrint")){
            byte[] data = args.getArrayBuffer(0);
            return executePrint(data, callbackContext);
        }
        return false;
    }
}
