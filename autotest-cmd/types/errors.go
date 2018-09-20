/*********************
	Error 定义
 *********************/

package types

// Story 模块
const (
	ERR_CASE_PREPARE                =   " Prepare data error : "
	ERR_CASE_EXECUTE       	        =   " Case execute error : "
	ERR_CASE_GETACTUAL          	=   " Get actual data error : "
	ERR_CASE_COMPARE                =   " Compare data error : "
	ERR_CASE_CLEANUP          		=   " Cleanup data error : "
)

// Keys
const (
	ERR_KEYS_DATAUNFOUND            =   " Data not found in the list (getActualData) : "
)

// Bank 模块
const (
)

// Validator 模块
const (
	ERR_VALIDATOR_ZERO              =   " 0 Validator : "
)

// Gov 模块
const (
	ERR_GOV_ZEROPROPOSAL              =   " 0 proposal : "
)



// Request 模块
const (
    ERR_REQUEST                     =   " Request error : "
    ERR_STATUSCODE 	                =   " Status code error : "
    ERR_UNMARSHAL 				    =   " UnMarshal repBody error : "
)


// Common 基本操作模块
const (
	ERR_SHOW_ACCOUNT_INFO           =  " Show Account Info (ShowAccountInfo) : "
	ERR_DELETE_ALL				    =  " Account delete all (KeysDeleteALL) : "
	ERR_INQUIRY_KEYLIST             =  " Inquiry keys list (QueryAccountList) : "
	ERR_DELETE_ACCOUNT              =  " Delete account (DeleteAccount) : "
	ERR_UPDATE_ACCOUNT              =  " Update account password (UpdateAccount) : "
	ERR_ADD_ACCOUNT                 =  " Add account (AddAccount) : "
	ERR_SEND_IRIS                   =  " Send iris (SendIris) : "
	ERR_GET_ACCOUNTINFO_FROMNAME    =  " Get account info from name (getAccountInfoFromName) : "
	ERR_GET_ACCOUNTINFO_FROMADDRESS =  " Get account info from address (getAccountInfoFromAddress) : "
	ERR_GET_ACCOUNTIRIS             =  " Get account iris amount (GetAccountIris) : "
	ERR_GET_TXINFO                  =  " Get TX info result (QueryTxResult) : "

	ERR_QUERY_VALIDATORLIST         =  " Query validator list (QueryValidatorList) : "
	ERR_INQUIRY_DELEGATION			=  " Inquiry delegation info (InquiryDelegation) : "
	ERR_TX_DELEGATION				=  " TX delegation info (TxDelegation) : "

	ERR_SUBMIT_PROPOSAL				=  " Submit proposal (SubmitProposal) : "
	ERR_QUERY_PROPOSALS				=  " Query proposals (QueryProposals) : "
	ERR_INQUIRY_PROPOSAL			=  " Inquiry proposal info (InquiryProposal) : "
	ERR_DEPOSIT_PROPOSAL			=  " Deposit proposal (DepositProposal) : "
	ERR_VOTE_PROPOSAL				=  " Vote proposal (VoteProposal) : "
	ERR_INQUIRY_VOTE				=  " Inquiry vote proposal (InquiryVote) : "

	ERR_DECODE_AMOUNT               =  " Decode amount error (DecodeAmount) : "
	ERR_GET_REPORTDATA              =  " Get report data error (GetReportData) : "

	ERR_GET_VERSION                 =  " Get Version error (GetVersion) : "
)

// FAUCET 模块
const (
	ERR_FAUCET                     =  " Faucet : "  //水龙头
	ERR_SETSID                     =  " Set SID error : "
)

// LOGGER 模块
const (
	ERR_LOGGER                     =  " Logger : "
	ERR_LOGGER_HTML                =  " Logger html : "
	ERR_LOGGER_XLS                 =  " Logger xls : "
	ERR_CASENUM_OVERFLOW_Z         =  " Number of subcases (z) > 99 : "
	ERR_CASENUM_OVERFLOW_Y         =  " Number of subcases (y) > 99 : "
	ERR_NOCASE_RUNNED              =  " NO case detected and run : "
)

// Config 模块
const (
	ERR_CONFIG                     =  " Config : "
	ERR_CONFIG_READFILE            =  " Read file : "
)
