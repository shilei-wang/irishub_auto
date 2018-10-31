package utils

import (
	. "github.com/irishub_auto/autotest-cmd/common"
	"time"
	"fmt"
	"encoding/json"
	"strings"
)

func ForTest(){
	Params = []string{"keys", "list"}

	repBody,  err := Common.RequestWorker.MakeRequest("iriscli", Params, nil)
	if err != nil {
		fmt.Print("  Test  c.RequestWorker.MakeRequest ")
		return
	}

	fmt.Println(repBody)
}

func Init_gentx(){
	Command = "iris"
	users := []string{"iris1","iris2","iris3","iris4"}
	names := []string{"v1","v2","v3","v4"}

	for i, user := range users {
		time.Sleep(time.Duration(DURATION)*time.Second)

		Params = []string{"init", "gen-tx","--name="+names[i],"--home="+ROOT+user}
		Common.RequestWorker.MakeRequest("iris", Params, nil)
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

func Copy_gentx() error {
	Params = []string{"iris2","iris3","iris4"}

	for _, param := range Params {
		if Err := CopyDir(ROOT+param+"\\config\\gentx\\", ROOT+"iris1\\config\\gentx\\"); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	fmt.Println("CopyFile : 3 gentx dirs to iris1 ok !")
	return nil
}

func Init_gentxs() {
	Command = "iris"
	Params = []string{"init", "--gen-txs","--chain-id=shilei-qa","-o","--home="+ROOT+"iris1"}
	Common.RequestWorker.MakeRequest("iris", Params, nil)
}

func ModifyGenesis() error{
	str  := ""
	file := ROOT+"iris1\\config\\genesis.json"

	if str,Err = read(file); Err != nil {
		return Err
	}

	str = strings.Replace(str, "\"amount\": \"100000000000000000000\"", "\"amount\": \"19840215000000000000000000\"", 4)
	str = strings.Replace(str, "\"voting_period\": \"20000\"", "\"voting_period\": \"20\"", 4)
	str = strings.Replace(str, "\"switch_period\": \"57600\"", "\"switch_period\": \"100\"", 4)

	if Err := write(file, str); Err != nil {
		fmt.Println(Err.Error())
		return Err
	}

	return nil
}

func CopyGenesis() error {
	Params = []string{"iris2","iris3","iris4"}

	for _, param := range Params {
		if _, Err := CopyFile(ROOT+"iris1\\config\\genesis.json", ROOT+param+"\\config\\genesis.json"); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}

func ModifyToml(node string) error{
	str  := ""
	file := ""
	param_files := []string{"iris2","iris3","iris4"}
	param_ports := []string{"6","7","8"}

	for i, p_f := range param_files {
		file = ROOT+p_f+"\\config\\config.toml"

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

func StartAndPrint(num int){
	Params := []string{"iris1","iris2","iris3","iris4"}

	for i, param := range Params {
		if i == num {break}

		time.Sleep(time.Duration(DURATION)*time.Second)

		Params = []string{"start", "--home="+ROOT+param}

		if (param == "iris1"){
			go Common.RequestWorker.ExecStart("iris", Params, true )
		}else{
			go Common.RequestWorker.ExecStart("iris", Params, false )
		}
	}

	time.Sleep(time.Duration(DURATION)*time.Second)
}

func Reset(c *CommonWorker){
	Command = "iris"
	users := []string{"iris1","iris2"}

	for _, user := range users {
		time.Sleep(time.Duration(DURATION)*time.Second)

		Params = []string{"unsafe_reset_all", "--home="+ROOT+user}
		//c(Command, Params,true,false)
	}
}