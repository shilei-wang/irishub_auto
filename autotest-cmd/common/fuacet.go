package common

import (
	. "bianjie-qa/irishub/autotest-cmd/types"
	. "bianjie-qa/irishub/autotest-cmd/utils"
)

var Faucet = faucet{}

type faucet struct {
	Common CommonWorker
}

func (f *faucet) Init(){
	f.Common = CommonWorker{}

	if Config.Map["Reset"] == "true" {
		Debug(MSG_FAUCET_DELETEALL, DEBUG_MSG)
		err := f.Common.KeysDeleteALL()
		if err != nil {
			panic(ERR_FAUCET+err.Error())
		}
	}

	// 检查FAUCET用户是否存在， FAUCET用户负责分发测试币给各模块各自的水龙头（例如：FAUCET_BANK，FAUCET_ACCOUNT）
	showResp, err := f.Common.ShowAccountInfo(FAUCET)
	if showResp == nil {
		Debug(ERR_FAUCET+err.Error(), DEBUG_MSG)
		Debug(MSG_FAUCET_INIT_START,DEBUG_MSG)

		err := f.Common.KeysDeleteALL()
		if err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err := f.Common.AddAccount(FAUCET)
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(VALIDATOR_1)
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_BANK)
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_STAKE)
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_GOV)
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_ACCOUNT)
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_FEE)
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}


		//从FAUCET 转账一定数量 iris 给 FAUCET_BANK
		_, err = f.Common.SendIris(FAUCET)
		if err != nil {//失败
			panic(ERR_FAUCET+err.Error())
		}

		//Sleep(SPACE_FAUCET)
	}

	showResp, err = f.Common.ShowAccountInfo(VALIDATOR_1)
	if showResp == nil {
		panic(ERR_FAUCET+err.Error())
	} else {
		//V1_ADDRESS = showResp.Address
	}

	Debug(MSG_FAUCET_INIT_OK, DEBUG_MSG)
}








