package requester

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"
	"net/http"
	"time"
	"encoding/json"
	"io"
	"bytes"
	"io/ioutil"
)

type Header struct {
	Key     		string
	Value   		string
}

type Requester struct {
	client         *http.Client
	request        *http.Request
	requestBody    []byte

	url            string
	method         string
	header         *Header
}

func (r *Requester)Init(url string, method string, header *Header){
	r.client = &http.Client{Timeout: time.Duration(RequestTimeout) * time.Second}
	r.url = url
	r.method = method
	r.header = header
}

func (r *Requester) MakeRequest(v interface{}) ([]byte , int,  error){
	resBody , err := json.Marshal(v)
	if err != nil {
		return nil, 0, err
	}

	var b io.Reader
	if resBody != nil {
		b = bytes.NewBuffer(resBody)
	} else {
		b = nil
	}

	req, err := http.NewRequest(r.method, r.url, b)
	if err != nil {
		return nil, 0, err
	}

	if r.header != nil {
		req.Header.Add(r.header.Key, r.header.Value)
	}

	resp, err := r.client.Do(req)
	if err != nil {
		return nil, 0, err
	}
	defer resp.Body.Close()

	respBody, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, 0, err
	}

	return respBody, resp.StatusCode, nil
}
