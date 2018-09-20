package tools

import (
	"time"
	"encoding/json"
	"fmt"
	"strings"
)

func Init_gentx(){
	Command = "iris"
	users := []string{"iris1","iris2","iris3","iris4"}
	names := []string{"x1","x2","x3","x4"}

	for i, user := range users {
		time.Sleep(time.Duration(DURATION)*time.Second)

		Params = []string{"init", "gen-tx","--name="+names[i],"--home="+ROOT+user}
		ExecCommand(Command, Params,false,false)
	}

	time.Sleep(time.Duration(DURATION)*time.Second)
}

func SetNodeInfo() error{
	Params := []string{"iris1","iris2","iris3","iris4"}

	for i, param := range Params {
		var str string
		if str,Err = ReadGentxFile(param); Err != nil {
			return Err
		}
		repData := &GenesisTx{}
		Err = json.Unmarshal( []byte(str), repData)

		fmt.Println(repData.NodeID)
		fmt.Println(repData.AppGenTx.Address)
		Nodes[i+1] = repData.NodeID
		Vaddrs[i+1] = repData.AppGenTx.Address
	}

	return nil
}

func CopyConfigGenesis() error {
	Params = []string{"iris2","iris3","iris4"}

	for _, param := range Params {
		if _, Err := CopyFile(ROOT+"iris1/config/genesis.json", ROOT+param+"/config/genesis.json"); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}

func WriteToml(node string) error{
	str  := ""
	file := ""
	param_files := []string{"iris2","iris3","iris4"}
	param_ports := []string{"6"    ,"7"    ,"8"}

	for i, param_file := range param_files {
		file = ROOT+param_file+"/config/config.toml"

		if str,Err = read(file); Err != nil {
			return Err
		}

		str = strings.Replace(str, "26656", "266"+param_ports[i]+"6", -1)
		str = strings.Replace(str, "26657", "266"+param_ports[i]+"7", -1)
		str = strings.Replace(str, "26658", "266"+param_ports[i]+"8", -1)
		str = strings.Replace(str, "seeds = \"\"", "seeds = \""+node+"@localhost:26656\"", -1)

		if Err := write(file, str); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}

func Copy_gentx() error {
	Params = []string{"iris2","iris3","iris4"}

	for _, param := range Params {
		if Err := CopyDir(ROOT+param+"/config/gentx/", ROOT+"iris1/config/gentx/"); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}

func Init_gentxs() {
	Command = "iris"
	Params = []string{"init", "--gen-txs","--chain-id=upgrade-test","-o","--home="+ROOT+"iris1"}
	ExecCommand(Command, Params,false,false)
}

func Run_4_IRIS(version string){
	Params := []string{"iris1","iris2","iris3","iris4"}

	for _, param := range Params {
		time.Sleep(time.Duration(DURATION)*time.Second)

		if version == "old" {
			Command = "iris"
		} else if version == "new" {
			Command = "iris1"
		}

		Params = []string{"start", "--home="+ROOT+param}
		go ExecCommand(Command, Params,false,false)
	}

	time.Sleep(time.Duration(DURATION)*time.Second)
}

func Print_iris_status (){
	for i:=0;i<6;i++ {
		time.Sleep(time.Duration(6)*time.Second)

		fmt.Println("Please check if the block chain is ok ? is the height increasing ?")
		Command = "iriscli"
		Params = []string{"status"}
		ExecCommand(Command, Params,true,false)
	}
}

func SubmitProposal(){
	Command = "iriscli"
	Params = []string{"gov", "submit-proposal","--name=x1","--proposer="+Vaddrs[1],"--title=ADD","--description=“I am crazy”","--type=SoftwareUpgrade","--deposit=10000000000000000000iris","--chain-id=upgrade-test","--fee=20000000000000000iris"}
	ExecCommand(Command, Params,true,true)
}

func VoteProposal(id string){
	fmt.Println("***********************************************")
	fmt.Println("           wait 5 seconds,and then vote !")
	fmt.Println("***********************************************")

	time.Sleep(time.Duration(5)*time.Second)


	users := []string{"x1","x2","x3","x4"}

	for i, user := range users {
		time.Sleep(time.Duration(2)*time.Second)
		Command = "iriscli"
		Params = []string{"gov", "vote","--name="+user,"--voter="+Vaddrs[i+1],"--proposalID="+id,"--option=Yes", "--chain-id=upgrade-test","--fee=20000000000000000iris"}
		ExecCommand(Command, Params,true,true)
	}
}

func InquiryProposal(id string){
	fmt.Println("***********************************************")
	fmt.Println("           Wait 30 block times ")
	fmt.Println("***********************************************")

	Command = "iriscli"
	Params = []string{"gov", "query-proposal","--proposalID="+id}

	for {
		time.Sleep(time.Duration(5)*time.Second)
		str := ExecCommand(Command, Params,false,false)
		inquiryStatus()
		fmt.Println(str)

		if strings.Contains(str, "Passed") {
			fmt.Println("***********************************************")
			fmt.Println("           Vote Passed !!!!!!!!!!!!!!")
			fmt.Println("***********************************************")
			break
		}

	}
}

func Kill_iris(){
	Command = "pkill"
	Params = []string{"-f", "iris"}
	ExecCommand(Command, Params,false,false)
}

func SubmitSwitch(id string){
	time.Sleep(time.Duration(5)*time.Second)
	users := []string{"x1","x2","x3","x4"}

	for i, user := range users {
		time.Sleep(time.Duration(2)*time.Second)
		Command = "iriscli1"
		Params = []string{"upgrade", "submit-switch","--name="+user,"--from="+Vaddrs[i+1],"--proposalID="+id, "--chain-id=upgrade-test","--fee=20000000000000000iris"}
		ExecCommand(Command, Params,true,true)
	}

}

func InquirySwitch(){
	fmt.Println("***********************************************")
	fmt.Println("           Wait 30 block times ")
	fmt.Println("***********************************************")

	Command = "iriscli1"
	Params = []string{"upgrade", "info"}

	for {
		time.Sleep(time.Duration(5)*time.Second)
		str := ExecCommand(Command, Params,false,false)
		inquiryStatus()
		fmt.Println(str[0:95])

		if strings.Contains(str, "\"Id\": \"1\"") {
			fmt.Println("***********************************************")
			fmt.Println("           Switch Passed !!!!!!!!!!!!!!")
			fmt.Println("***********************************************")
			break
		}

	}
}

func CheckNewModule(){
	Command = "iriscli1"
	Params = []string{"advanced", "ibc","set","--name=x1","--from="+Vaddrs[1],"--chain-id=upgrade-test","--sequence=0","--print-response","true","--fee=20000000000000000iris"}
	str := ExecCommand(Command, Params,true,true)


	if strings.Contains(str, "This is new module") {
		fmt.Println("***********************************************")
		fmt.Println("          Check upgrade ok!!!!!!!!!  ")
		fmt.Println("***********************************************")
	}
}

func inquiryStatus(){
	str := ExecCommand("iriscli", []string{"status"},false,false)
	str = GetBetweenStr(str, "\"latest_block_height",",\"catching_up")
	fmt.Println(str)
}