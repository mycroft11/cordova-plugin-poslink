//
//  ManageResponse.h
//  PosLink
//
//  Created by sunny on 15-11-15.
//  Copyright (c) 2015年 pax. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface ManageResponse : NSObject

/**
 * Result code of transaction.
 * <p>Used to determine results of transaction.<br>
 */
@property NSString*ResultCode;
/**
 * Result Txt of transaction.
 */
@property NSString*ResultTxt;
/**
 * Terminal SN.
 * <p>Valid while request transaction is INIT.<br>
 */
@property NSString* SN;
/**
 * The value of the variable.
 * <p>Valid while request transaction is GETVAR.<br>
 */
@property NSString* VarValue;
/**
 * The value of the variable.
 * <p>Valid while request transaction is GETVAR.<br>
 */
@property NSString* VarValue1;
/**
 * The value of the variable.
 * <p>Valid while request transaction is GETVAR.<br>
 */
@property NSString* VarValue2;
/**
 * The value of the variable.
 * <p>Valid while request transaction is GETVAR.<br>
 */
@property NSString* VarValue3;
/**
 * The value of the variable.
 * <p>Valid while request transaction is GETVAR.<br>
 */
@property NSString* VarValue4;
/**
 * The button number of selection.
 * <p>Valid while request transaction is SHOWDIALOG.<br>
 */
@property NSString*ButtonNum;
/**
 * FileName of captured signature.
 * <p>Ex: 201108111743.bmp<br>
 * It will be saved at &lt;execute directory&gt;\img\receipts\201108111743.bmp<br>
 * Valid while request transaction is GETSIGANTURE.<br>
 */
@property NSString* SigFileName;
/**
 *Signature status:
 *1: done the signature.
 * Valid while request transaction is SHOWTEXTBOX.<br>
 */
@property NSString* SignStatus;
/**
 * The PIN BLOCK.
 * <p>Valid while request transaction is GETPINBLOCK.<br>
 */
@property NSString*PinBlock;
/**
 * The KSN.
 * <p>Valid while request transaction is GETPINBLOCK or INPUTACCOUNT.<br>
 */
@property NSString* KSN;
/**
 * The Entry Mode.
 * <p>Valid while request transaction is INPUTACCOUNT.<br>
 */
@property NSString*EntryMode;
/**
 * Track1 data (May be encrypted.)
 * <p>Valid while request transaction is INPUTACCOUNT.<br>
 */
@property NSString* Track1Data;
/**
 * Track2 data (May be encrypted.)
 * <p>Valid while request transaction is INPUTACCOUNT.<br>
 */
@property NSString*Track2Data;
/**
 * Track3 data (May be encrypted.)
 * <p>Valid while request transaction is INPUTACCOUNT.<br>
 */
@property NSString*Track3Data;
/**
 * Account Number for manual entry (May be encrypted.)
 * <p>Valid while request transaction is INPUTACCOUNT.<br>
 */
@property NSString*PAN;
/**
 * The expiration date for manual entry in format of MMYY.
 * <p>Valid while request transaction is INPUTACCOUNT.<br>
 */
@property NSString*ExpiryDate;
/**
 * QR code from scanner in plain text.
 * <p>Valid while request transaction is INPUTACCOUNT.<br>
 */
@property NSString* QRCode;
/**
 * Text input.
 * <p>Valid while request transaction is INPUTTEXT.<br>
 */
@property NSString*Text;
/**
 * Indicate the transactin result after 1st GAC
 * <p>0: transaction offline approved<br>
 *    1: transaction offline declined<br>
 *    2: transaction online authorization needed<br>
 *    valid when the request transaction = AUTHORiZECARD or COMPLETEONLINEEMV<br>
 */
@property NSString*AuthorizationResult;
/**
 * Indicate taht if signature is needed:
 * <p>0: Signature NOT needed<br>
 *    1: Signature needed<br>
 *    valid when the request transaction = AUTHORiZECARD or COMPLETEONLINEEMV<br>
 */
@property NSString*SignatureFlag;
/**
 * If contact EMV card is read successfully, some reloated EMV data will be returned in consecutive TLV format data
 *    valid when the request transaction = AUTHORiZECARD or COMPLETEONLINEEMV or INPUTACCOUNTWITHEMV<br>
 */
@property NSString*EMVTLVData;
@property NSString*TagList;

/**
 * <p>Valid while request transaction is AUTHORiZECARD.<br>
 */
@property NSString*PINBypassStatus;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*MaskedPAN;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*BarcodeType;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*BarcodeData;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*ETB;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*ContactlessTransactionPath;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*OnlinePINFlag;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*EncryptedEMVTLVData;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*EncryptedSensitiveTLVData;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*CardHolderName;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*ServiceCode;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*CVVCode;
/**
 * <p>Valid while request transaction is INPUTACCOUNTWITHEMV.<br>
 */
@property NSString*ZipCode;

/**
 * Model Name
 * <p>Valid while request transaction is INIT.<br>
 */
@property NSString*ModelName;
/**
 * OS Version
 * <p>Valid while request transaction is INIT.<br>
 */
@property NSString*OSVersion;
/**
 * MacAddress for LAN module in text
 * <p>Valid while request transaction is INIT.<br>
 */
@property NSString*MacAddress;
/**
 * WIFI Mac Address for WIFI module in text
 * <p>Valid while request transaction is INIT.<br>
 */
@property NSString*WifiMac;
/**
 * Number of lines per screen for ShowMessage command
 * <p>Valid while request transaction is INIT.<br>
 */
@property NSString*LinesPerScreen;
/**
 * Number of characters per line for ShowMessage command
 * <p>Valid while request transaction is INIT.<br>
 */
@property NSString*CharsPerLine;
/**
 * Status of card insert.
 * <p>Valid while request transaction is CARDINSERTDETECTION.<br>
 */
@property NSString*CardInsertStatus;
/**
 * Extended data in XML format
 */
@property(nonatomic,copy) NSString*ExtData;
/**
 * signature data
 */
@property NSString *signatureData;
/**
 * Label Selected
 */
@property NSString *LabelSelected;
-(int)unpack:(NSArray*)dataRespArry;

@end
