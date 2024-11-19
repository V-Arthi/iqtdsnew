import React, { useState } from 'react'
import { Form, Input, Button, Checkbox,Alert } from 'antd'
import {ArrowRightOutlined} from '@ant-design/icons'
import '../styles/home.css'
import bcrypt from 'bcryptjs'
import axios from "axios";

const config = require('../config.json')
const endpoint = process.env.REACT_APP_API_URL;
const apiUrl = `${endpoint}/user/add`;
const emptyAlert = { type: "info", message: "" };
const CONNECTION_ERROR =
  "Error in connecting to api server, Please contact administrator";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
}

const tailLayout = {
    wrapperCol: { offset: 8, span: 16 },
}

function CreateUser() {

    const [data, setData] = useState({ username: '', password: '', displayName:'', email:'', role:'', active:true });
    const [showErr,setShowErr] = useState(false)
    const [alert, setAlert] = useState(emptyAlert);

    // const salt = bcrypt.genSaltSync(10)
    // console.log(salt)
    // console.log(config.salt)
    // const hashedPassword = bcrypt.hashSync(user.pass, config.salt)
    console.log(data)

    const signup=(e)=>{
        
        e.preventDefault();
        axios
        .post(apiUrl, data)
        .then((res) => {
          setAlert({ type: "success", message: res.data });
          console.log(res.data)
          setShowErr(false)
        })
        .catch((err) => {
            console.log("Catch Block")
          if (err.response) {
            setAlert({ type: "error", message: err.response.data });
          } else {
            setAlert({ type: "error", message: CONNECTION_ERROR });
          }
          setShowErr(true)
          return
        });
    }

    // useEffect(()=>{console.clear()},[])

    return (
        <div className="home">

            {
                showErr && <Alert
                type={alert.type}
                message={alert.message}
                showIcon
                closable
                onClose={() => setAlert({ type: "info", message: "" })}
                style={{ marginBottom: 30 }}
              />
            }

          <Form
                {...layout}
                name="login"
                initialValues={{ remember: true }}   
                autoComplete="off"
                className="login_form"
          >
           
            <Form.Item
                label="Username"
                name="username"
                rules={[{ required: true, message: 'Please input username!' }]}
                
            >
                <Input value={data.username} onChange={e=> {setData({...data,username:e.target.value})}
            }/>
            </Form.Item>

            <Form.Item
                label="Password"
                name="password"
                rules={[{ required: true, message: 'Please input password!' }]}
            >
                <Input.Password value={data.password} onChange={e=>{setData({...data,password:bcrypt.hashSync(e.target.value, config.salt)})}
            }/>
            </Form.Item>

            <Form.Item
                label="Display Name"
                name="displayName"
                rules={[{ required: true, message: 'Please input Display Name!' }]}
            >
                <Input value={data.displayName} onChange={e=>{setData({...data,displayName:e.target.value})}
            }/>
            </Form.Item>

            <Form.Item
                label="Email"
                name="email"
                rules={[{ required: true, message: 'Please input EH Email!' }]}
            >
                <Input value={data.email} onChange={e=>{setData({...data,email:e.target.value})}
            }/>
            </Form.Item>

            <Form.Item
                label="Role"
                name="role"
                rules={[{ required: true, message: 'Please input Role!' }]}
            >
                <Input value={data.role} onChange={e=>{setData({...data,role:e.target.value})}
            }/>
            </Form.Item>

            <Form.Item {...tailLayout} name="active" valuePropName="checked">
                <Checkbox>Active</Checkbox>
            </Form.Item>

            <Form.Item {...tailLayout}>
                <Button htmlType="submit" onClick={signup} icon={<ArrowRightOutlined />}>Signup</Button>
            </Form.Item>
        </Form>
        </div>
    )
}

export default CreateUser