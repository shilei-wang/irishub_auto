package utils

import (
	. "bianjie-qa/irishub/autotest-cmd/types"

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
}

func readFile(filename string) (map[string]string, error) {
	bytes, err := ioutil.ReadFile(filename)
	if err != nil {
		return nil, errors.New(ERR_CONFIG_READFILE + err.Error())
	}

	m := map[string]string{}
	if err := json.Unmarshal(bytes, &m); err != nil {
		return nil, errors.New(ERR_CONFIG + ERR_REQUEST + err.Error())
	}

	return m, nil
}


