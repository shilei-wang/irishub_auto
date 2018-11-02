package stories

import (
	. "github.com/irishub_auto/autotest-cmd/types"
	. "github.com/irishub_auto/autotest-cmd/utils"
	"fmt"
)

type DefinitionStress struct {
	Story
}


func (s *DefinitionStress) prepareEnv(subCase *SubCase) {
}

func (s *DefinitionStress) cleanupEnv(subCase *SubCase){
}

func (s *DefinitionStress) Run(){
	s.Init()
	//s.forTest()
	fileName := "d:\\testDefinition.proto"

	nums := 500
	fmt.Println("time estimation ",fmt.Sprintf("%.1f", float32(nums)*12/3600),"  hours")
	randomid := RandomId()

	for i := 0; i < nums; i++ {
		serviceName := fmt.Sprintf("%s_v1_%v", randomid, i)
		s.Common.DefineDifinition(serviceName,fileName, "v1")
		fmt.Println("finish : "+serviceName)

		serviceName = fmt.Sprintf("%s_v2_%v", randomid, i)
		s.Common.DefineDifinition(serviceName,fileName, "v2")
		fmt.Println("finish : "+serviceName)

		serviceName = fmt.Sprintf("%s_v3_%v", randomid, i)
		s.Common.DefineDifinition(serviceName,fileName, "v3")
		fmt.Println("finish : "+serviceName)

		serviceName = fmt.Sprintf("%s_v4_%v", randomid, i)
		s.Common.DefineDifinition(serviceName,fileName, "v4")
		fmt.Println("finish : "+serviceName)
	}

	//time.Sleep(time.Duration(24)*time.Hour)
}

func (s *DefinitionStress) startWork(nums int, random string,name string){
	/*
	fileName := "d:\\testDefinition.proto"
	//ioutil.WriteFile(fileName, []byte(idlContent), 0644)

	for i := 0; i < nums; i++ {
		serviceName := fmt.Sprintf("service_%s_%v", random, i)
		s.Common.DefineDifinition(serviceName,fileName, name)
		fmt.Println("finish : "+serviceName)
		time.Sleep(time.Duration(7)*time.Second)
	}
	*/
}

func (s *DefinitionStress) forTest(){
	repBody,  err := s.Common.GetVersion()
	if err != nil {
		fmt.Print("Error check Irishub first!")
		return
	}

	fmt.Println(repBody)
}

const idlContent =
`syntax = "proto3";

// The greeting service definition.
service Greeter {
	//@Attribute description:sayHello
	//@Attribute output_privacy:NoPrivacy
	//@Attribute output_cached:NoCached
	rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
	string name = 1;
}

// The response message containing the greetings
message HelloReply {
	string message = 1;
}`