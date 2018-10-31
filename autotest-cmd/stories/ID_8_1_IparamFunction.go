package stories

import (
	. "github.com/irishub_auto/autotest-cmd/types"
	"fmt"
)

type IparamFunction struct {
	Story
	//exceptedData [5]*AddAccountResp
}

func (s *IparamFunction) registerSubCases(){
	s.SubCases = append(s.SubCases, &SubCase{"8-1-1",s.case_8_1_1,UNKNOW,
		"Iparam - 查询(module) :",""})
	s.SubCases = append(s.SubCases, &SubCase{"8-1-2",s.case_8_1_2,UNKNOW,
		"Iparam - 查询(key-DepositProcedure) :",""})
	s.SubCases = append(s.SubCases, &SubCase{"8-1-3",s.case_8_1_3,UNKNOW,
		"Iparam - 查询(key-TallyingProcedure) :",""})
	s.SubCases = append(s.SubCases, &SubCase{"8-1-4",s.case_8_1_4,UNKNOW,
		"Iparam - 查询(key-VotingProcedure) :",""})
//	s.SubCases = append(s.SubCases, &SubCase{"8-1-5",s.case_8_1_5,UNKNOW,
//		"Iparam - 更新(submit-DepositProcedure) :",""})
//	s.SubCases = append(s.SubCases, &SubCase{"8-1-6",s.case_8_1_6,UNKNOW,
//		"Iparam - 更新(submit-TallyingProcedure) :",""})
//	s.SubCases = append(s.SubCases, &SubCase{"8-1-7",s.case_8_1_7,UNKNOW,
//		"Iparam - 更新(submit-VotingProcedure) :",""})
}

func (s *IparamFunction) prepareEnv(subCase *SubCase) error{
	if (*subCase).CaseID == "8-3-1"{
		//创建Bruce用户
		//resultData, err := s.Common.AddAccount(BRUCE)
		//if err != nil {
		//	return err
		//}

		//记录账户数据
		//if resultData == nil {}
		//s.exceptedData[0] = resultData
		fmt.Println("001")
	}

	return nil
}

func (s *IparamFunction) cleanupEnv(subCase *SubCase){
	if (*subCase).CaseID == "8-3-1"{
		//删除Bruce用户
		//err := s.Common.DeleteAccount(BRUCE)
		//if err != nil {
		//	Debug((*subCase).CaseID + ERR_CASE_CLEANUP + err.Error(), DEBUG_FAIL)
		//	return
		//}
		//
		//s.exceptedData[0] = nil
	}
}

func (s *IparamFunction) Run(){
	s.Init()
	s.registerSubCases()
	s.Traverse()
}

func (s *IparamFunction) case_8_1_1(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作

	// 获取实际数据
	ModuleList, err := s.Common.QueryIparamModule("gov")
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比(实际和期望)数据
	if err = s.Common.CompareString((*ModuleList)[0], "Gov/gov/DepositProcedure"); err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}
	if err = s.Common.CompareString((*ModuleList)[1], "Gov/gov/TallyingProcedure"); err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}
	if err = s.Common.CompareString((*ModuleList)[2], "Gov/gov/VotingProcedure"); err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *IparamFunction) case_8_1_2(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作

	// 获取实际数据
	keyResp, err := s.Common.QueryIparamKey("Gov/gov/TallyingProcedure")
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比(实际和期望)数据
	if err = s.Common.CompareString((*keyResp).Key, "Gov/gov/TallyingProcedure"); err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *IparamFunction) case_8_1_3(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作

	// 获取实际数据
	keyResp, err := s.Common.QueryIparamKey("Gov/gov/VotingProcedure")
	if err != nil {
		return FAIL, ERR_CASE_GETACTUAL + err.Error()
	}

	//对比(实际和期望)数据
	if err = s.Common.CompareString((*keyResp).Key, "Gov/gov/VotingProcedure"); err != nil {
		return FAIL, ERR_CASE_COMPARE + err.Error()
	}

	return PASS, ""
}

func (s *IparamFunction) case_8_1_4(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作

	// 获取实际数据

	//对比(实际和期望)数据

	return PASS, ""
}

func (s *IparamFunction) case_8_1_5(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作

	// 获取实际数据

	//对比(实际和期望)数据

	return PASS, ""
}

func (s *IparamFunction) case_8_1_6(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作

	// 获取实际数据

	//对比(实际和期望)数据

	return PASS, ""
}

func (s *IparamFunction) case_8_1_7(subCase *SubCase) (ResultType, string){
	// 构造环境 (获取期望数据)
	err := s.prepareEnv(subCase)
	defer s.cleanupEnv(subCase)
	if err != nil {
		return FAIL, ERR_CASE_PREPARE + err.Error()
	}

	// 执行操作

	// 获取实际数据

	//对比(实际和期望)数据

	return PASS, ""
}

