package utils

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-cmd/types"

	"io/ioutil"
	"encoding/json"
	"errors"
	"time"
)

var Config = config{}

type config struct {
	Map map[string]string
}

func (c *config) Init(){
	TIME_START = time.Now()
	var err error
	c.Map , err = readFile("conf.json")
	if err != nil {
		panic(ERR_CONFIG + err.Error())
	}

	NODE           =  NODE + c.Map["Node"]
	CHAINID        =  CHAINID + c.Map["ChainID"]
	TXAMOUNT       =  TXAMOUNT + c.Map["TxAmount"]
	TXGAS          =  TXGAS + c.Map["TxGas"]
	TXFEE          =  TXFEE + c.Map["TxFee"]
	FAUCET_SEED    =  c.Map["faucet_seed"]
	VALIDATOR_SEED =  c.Map["V1_seed"]
}

func readFile(filename string) (map[string]string, error) {
	bytes, err := ioutil.ReadFile(filename)
	if err != nil {
		return nil, errors.New(ERR_CONFIG_READFILE + err.Error())
	}

	m := map[string]string{}
	if err := json.Unmarshal(bytes, &m); err != nil {
		return nil, errors.New(ERR_CONFIG + err.Error())
	}
	return m, nil
}


