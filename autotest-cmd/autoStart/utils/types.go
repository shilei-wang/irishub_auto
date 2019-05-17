package utils

import (
	. "github.com/irishub_auto/autotest-cmd/common"
	"errors"
)

var Common = &CommonWorker{}

// global vars
var HOME        = "/Users/sherlock/"
var PASSWORD    = "1234567890"
var DURATION    = 2
var BLOCK_TIME  = "5"


//***********************************

var Err         = errors.New("")
var Params      = []string{}
var Command     = ""

// save 4 nodes and 5 validator address
var Nodes        [5]string
var Vaddrs       [5]string

// cosmos types
type GenesisTx struct {
	NodeID    string                   `json:"node_id"`
	IP        string                   `json:"ip"`
	Validator GenesisValidator         `json:"validator"`
	AppGenTx  GenTx                    `json:"app_gen_tx"`
}

type GenesisValidator struct {
	PubKey    string                   `json:"pub_key"`
	Power     int64                    `json:"power"`
	Name      string                   `json:"name"`
}


type GenTx struct {
	Name      string                   `json:"name"`
	Address   string                   `json:"address"`
	Pub_key   string                   `json:"pub_key"`
}

const faucet_init = ",{\"address\": \"faa1ljthexzckk3vzl9aqk730zlus2zww73mk2298g\",\"coins\": [{\"denom\": \"iris-atto\",\"amount\": \"199999999999999999999999999\"}]}"

