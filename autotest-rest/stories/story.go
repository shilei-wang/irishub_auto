package stories

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/common"
)

type Method interface {
	Init()
	Traverse()
	Execute()
	finish()
}

type Story struct{
	// 继承Method接口
	Method
	// 基本行为类
	Common CommonWorker
	// 存储具体case的信息结构
	SubCases []*SubCase
}

func (s *Story) Init() {
	s.Common = CommonWorker{}
}

func (s *Story) Traverse() {
	for _, subCase := range s.SubCases {
		s.Execute(subCase)
	}
}

func (s *Story) Execute(subCase *SubCase) ResultType {
	defer s.finish(subCase)
	Debug("Start run case ===> "+(*subCase).CaseID + " : " +(*subCase).Description, DEBUG_MSG)

	// 具体执行case的方法，返回执行结果和debug信息
	(*subCase).ActualResult, (*subCase).DebugInfo = (*subCase).StartProcess(subCase)

	// 如果结果为FAIL 调用FailureHandler方法
	if (*subCase).ActualResult == FAIL {
		Debug((*subCase).CaseID + (*subCase).DebugInfo, DEBUG_FAIL)
	}

	return (*subCase).ActualResult
}

func (s *Story) finish(subCase *SubCase){
	// 把case执行结果信息，插入入report单向列表
	Logger.InsertList(subCase)
}

