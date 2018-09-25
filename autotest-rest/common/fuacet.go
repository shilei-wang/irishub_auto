package common

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
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
		Debug(ERR_FAUCET, DEBUG_MSG)
		Debug(MSG_FAUCET_INIT_START,DEBUG_MSG)

		err := f.Common.KeysDeleteALL()
		if err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err := f.Common.AddAccount(FAUCET, PASSWORD ,Config.Map["faucet_seed"])
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(VALIDATOR_1, PASSWORD ,Config.Map["V1_seed"])
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_BANK, PASSWORD ,"")
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_STAKE, PASSWORD ,"")
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_GOV, PASSWORD ,"")
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_ACCOUNT, PASSWORD ,"")
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		addResp, err = f.Common.AddAccount(FAUCET_FEE, PASSWORD ,"")
		if err != nil || addResp == nil{
			panic(ERR_FAUCET+err.Error())
		}

		err = f.Common.SetSID()
		if err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		//从FAUCET 转账一定数量 iris 给 FAUCET_BANK
		_, err = f.Common.SendIris(FAUCET, FAUCET_BANK, InitIris, nil)
		if err != nil {//失败
			panic(ERR_FAUCET+err.Error())
		}

		//从FAUCET 转账一定数量 iris 给 FAUCET_STAKE
		_, err = f.Common.SendIris(FAUCET, FAUCET_STAKE, InitIris, nil)
		if err != nil {//失败
			panic(ERR_FAUCET+err.Error())
		}

		//从FAUCET 转账一定数量 iris 给 FAUCET_GOV
		_, err = f.Common.SendIris(FAUCET, FAUCET_GOV, InitIris, nil)
		if err != nil {//失败
			panic(ERR_FAUCET+err.Error())
		}

		//从FAUCET 转账一定数量 iris 给 FAUCET_ACCOUNT
		_, err = f.Common.SendIris(FAUCET, FAUCET_ACCOUNT, InitIris, nil)
		if err != nil {//失败
			panic(ERR_FAUCET+err.Error())
		}

		//从FAUCET 转账一定数量 iris 给 FAUCET_FEE
		_, err = f.Common.SendIris(FAUCET, FAUCET_FEE, InitIris, nil)
		if err != nil {//失败
			panic(ERR_FAUCET+err.Error())
		}

		Sleep(SPACE_FAUCET)
	} else {
		err = f.Common.SetSID()
		if err != nil {
			panic(ERR_FAUCET+err.Error())
		}
	}

	showResp, err = f.Common.ShowAccountInfo(VALIDATOR_1)
	if showResp == nil {
		panic(ERR_FAUCET+err.Error())
	} else {
		V1_ADDRESS = showResp.Address
	}

	Debug(MSG_FAUCET_INIT_OK, DEBUG_MSG)
}








