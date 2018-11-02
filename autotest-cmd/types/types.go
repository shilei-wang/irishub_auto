package types

import (
	"sync"
	"time"
)

/*********************
	最后检查一下所有灰色项
*********************/

/*********************
	关键参数
*********************/
// 1) 全局变量
var NODE           = "--node="      //"--node=http://localhost:26657"
var CHAINID        = "--chain-id="  //"--chain-id=upgrade-test"
var TXAMOUNT       = "--amount="    //"--amount=1iris"
var TXFEE          = "--fee="       //"--fee=0.004iris"
var TXGAS          = "--gas="      //"--gas=200000"

var COMMAND_CLI    = "iriscli"
var COMMAND_IRIS   = "iris"
var JSON           = "--json"
var TRUSTNODE      = "--trust-node"
var FAUCET_SEED    = ""
var VALIDATOR_SEED = ""

var VALIDATOR_NAME = "v1"


// 2)


// 3)
const CMD_TIMEOUT    = 20

// 4) 出块等待时间 5s , RequestTimeout 超时时间 20s
const WaitBlockTime  = 8

// 5) Proposal最小抵押金额
const MinDeposit    = "10"

// 6) 水龙头首次分配给“子story”代币数量
const InitIris      = "100000iris"

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
	FAUCET            = "faucet"
	USER              = "user"
	VALIDATOR_1       = "V1"


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

// 4)
const (
	AddressPrefix   = "faa"
	AddressLen      = 42
)

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

	SPACE_MODULE_IPARAM  = 8

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
