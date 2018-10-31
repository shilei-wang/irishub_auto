package common

import (
	. "github.com/irishub_auto/autotest-cmd/types"
	. "github.com/irishub_auto/autotest-cmd/utils"
)

var Faucet = faucet{}

type faucet struct {
	Common CommonWorker
}

func (f *faucet) Init(){
	f.Common = CommonWorker{}

	if Config.Map["Reset"] == "true" {
		Debug(MSG_FAUCET_DELETEALL, DEBUG_MSG)
		if err := f.Common.KeysDeleteALL(); err != nil {
			panic(ERR_FAUCET+err.Error())
		}
	}

	// 检查FAUCET用户是否存在， FAUCET用户负责分发测试币给各模块各自的水龙头（例如：FAUCET_BANK，FAUCET_ACCOUNT）
	if _, err := f.Common.ShowAccountInfo(FAUCET); err != nil {
		Debug(MSG_FAUCET_INIT_START,DEBUG_MSG)

		if err := f.Common.KeysDeleteALL(); err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		if _, err := f.Common.AddAccount(FAUCET, PASSWORD, FAUCET_SEED); err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		if _, err := f.Common.AddAccount(VALIDATOR_1, PASSWORD, VALIDATOR_SEED); err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		if _, err := f.Common.AddAccount(USER, PASSWORD, ""); err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		//从FAUCET 转账一定数量 iris 给 USER
		_, err := f.Common.SendIris(FAUCET, USER, InitIris, nil)
		if err != nil {
			panic(ERR_FAUCET+err.Error())
		}

		Sleep(SPACE_FAUCET)
	}

	if respShow, err := f.Common.ShowAccountInfo(VALIDATOR_1); err == nil {
		V1_ADDRESS = respShow
	} else {
		panic(ERR_FAUCET+err.Error())
	}

	Debug(MSG_FAUCET_INIT_OK, DEBUG_MSG)
}








