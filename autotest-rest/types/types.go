package types

import (
	"sync"
	"time"
)

/*********************
	关键参数
*********************/
// 1) 总的模块数量
const NUMBER_OF_MODULES   = 6

// 2)

// 3) gas price (暂时预留)
// threshold  20000000000iris-atto (默认gas下 是0.004iris)
const MIN_GASPRICE = 20000000000

// 4) 出块等待时间 5s , RequestTimeout 超时时间 20s
const WaitBlockTime = 15  //默认8秒是没有问题的 12秒已经很慢了
const RequestTimeout = 20

// 5) Proposal最小抵押金额
const MinDeposit = "1000"

// 6) 水龙头首次分配给“子story”代币数量
const InitIris = "100000"

// 7) 默认Gas
const GasForSend = "200000"

// 8) 默认Fee
const GasForFee  = "0.004iris"

// 9) 默认TxAmount
const TxAmount  = "5"

// 10) 默认cointype
const DefaultCoinType  = "iris"

/*********************
	用例Mock变量
*********************/

// 1) 密码
const (
	PASSWORD    = "1234567890"
	NEWPASSWORD = "0987654321"
	BADPASSWORD = "0000000000"
)

// 2) 用户名 (每个story用不同的用户名组，避免冲突)
const (
	VALIDATOR_1       = "V1"

	FAUCET            = "faucet"
	FAUCET_BANK       = "faucet_bank"
	FAUCET_STAKE      = "faucet_stake"
	FAUCET_GOV        = "faucet_gov"
	FAUCET_ACCOUNT    = "faucet_account"
	FAUCET_FEE        = "faucet_fee"

	KEVIN			  = "Kevin"
	KENT 			  = "Kent"
	KATIE			  = "Katie"

	BRUCE			  = "Bruce"
	BOB 			  = "Bob"
	BILL			  = "Bill"

	STEVEN			  = "Steven"
	SIMON 			  = "Simon"
	SOFIA			  = "Sofia"

	ANTHONY 		  = "Anthony"
	ANDY    	      = "Andy"
	ANDREW  		  = "Andrew"

	FRANK             = "Frank"
)

// 3) validator 1
var V1_ADDRESS string

// 4) 自增益SID
var FAUCET_SID			 = 0
var FAUCET_BANK_SID 	 = 0
var FAUCET_STAKE_SID 	 = 0
var FAUCET_GOV_SID 	 	 = 0
var FAUCET_ACCOUNT_SID 	 = 0
var FAUCET_FEE_SID 	     = 0

// 3) 记录初始时间
var TIME_START  time.Time

/*********************
	其他全局变量
*********************/

// 1) Massage Space
const (
	SPACE_MODULE_KEYS    = 1
	SPACE_MODULE_BANK    = 2
	SPACE_MODULE_STAKE   = 3
	SPACE_MODULE_GOV     = 4
	SPACE_MODULE_ACCOUNT = 5
	SPACE_MODULE_FEE     = 6

	SPACE_FAUCET         = 10
)

// sleep
const (
	SLEEP_BLOCKTIME     = 1
	SLEEP_LONGER        = 2
)

const (
	DEBUG_FAIL    = 1
	DEBUG_MSG     = 2
)

const (
	DEFAULT       = "DEFAULT"
)

// 2) 同步等待锁
var   Wg sync.WaitGroup

// 3) Case运行结果
type  ResultType string
const (
	PASS ResultType = "PASS"
	FAIL ResultType = "FAIL"
	UNKNOW ResultType = "UNKNOW" //预留
)

// 4) SubCase 实例
type SubCase struct {
	CaseID               string
	StartProcess         func(*SubCase)(ResultType, string)  //具体执行case的方法
	ActualResult         ResultType
	Description          string
	DebugInfo            string
}

// 5) Report 实例
type ReportData struct {
	FaucetBalance string  // 水龙头余额
	Version       string
	ChainId       string
	TimeNow       string
}
