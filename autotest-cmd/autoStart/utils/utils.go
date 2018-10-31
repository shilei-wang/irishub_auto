package utils

import (
	"os"
	"fmt"
	"path/filepath"
	"errors"
	"io/ioutil"
	"strings"
	"io"
)

func Rm(params []string)error{
	for _, file := range params {
		f := ROOT+ file
		err := os.RemoveAll(f)
		if err != nil {
			return err
		}
	}

	fmt.Println("Files remove OK!")
	return nil
}

func ReadGentxFile(name string) (string, error) {
	files, _ := filepath.Glob(ROOT + name + "/config/gentx/*")
	if len(files) == 0{
		return "", errors.New("Read node error")
	}

	var str string
	if str,Err = read(files[0]); Err != nil {
		return "", Err
	}

	return str,nil
}

func CopyDir(src string, dest string)error{
	src_original := src
	src_file:= ""
	Err := filepath.Walk(src, func(src string, f os.FileInfo, Err error) error {
		if f == nil {
			return Err
		}

		if src == src_original {
			return nil
		}

		dest_new := strings.Replace(src, src_original, dest, -1)

		src_file = src

		_ , Err = CopyFile(src, dest_new)
		if Err != nil{
			return Err
		}

		return nil
	})

	if Err != nil {
		return errors.New("CopyDir error"+Err.Error())
	}

	//fmt.Println("CopyFile "+src_file+" ok !")
	return nil
}

func CopyFile(srcName, destName string)(int64, error){
	srcFile ,Err:= os.Open(srcName)
	if Err!=nil{
		return 0, Err
	}
	defer srcFile.Close()
	destFile,Err:=os.OpenFile(destName,os.O_WRONLY|os.O_CREATE,0777)
	if Err!=nil{
		return 0, Err
	}
	defer destFile.Close()
	return io.Copy(destFile,srcFile)
}

func read(path string) (string,error) {
	fi, Err := os.Open(path)
	if Err != nil {
		return "",Err
	}
	defer fi.Close()

	fd, Err := ioutil.ReadAll(fi)
	return string(fd),nil
}

func write(name,content string) error {
	data := []byte(content)
	if ioutil.WriteFile(name,data,0644) != nil {
		return errors.New(name+ "write file error")
	}
	fmt.Println("Modify "+ name + " file ok !")
	return nil
}

