import React, { useState,useContext, useEffect } from 'react'
import {Redirect} from 'react-router-dom'
import { Form, Input, Button, Checkbox,Alert } from 'antd'
import {ArrowRightOutlined} from '@ant-design/icons'
import {UserContext} from '../Contexts/UserContext'
import '../styles/home.css'
import bcrypt from 'bcryptjs'
import axios from "axios";

const config = require('../config.json')
const endpoint = process.env.REACT_APP_API_URL;
const apiUrl = `${endpoint}/user/login`;
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



function Home() {

    const [user,setUser] = useState({username:'',pass:'',remember:true})
    const [data, setData] = useState({ username: '', password: '' });
    const [showErr,setShowErr] = useState(false)
    const [alert, setAlert] = useState(emptyAlert);

    const {authUser,setAuthUser} = useContext(UserContext)

    // const salt = bcrypt.genSaltSync(10)
    // console.log(salt)
    // console.log(config.salt)
    // const hashedPassword = bcrypt.hashSync(user.pass, config.salt)
    // console.log(data)

    const login=(e)=>{
        
        e.preventDefault();
        axios
        .post(apiUrl, data)
        .then((res) => {
          setAlert({ type: "success", message: res.data });
          console.log(res.data)
          setShowErr(false)
        setAuthUser({name:data.username,role:res.data,authenticated:true})
        setUser({username:'',pass:'',remember:true})
        })
        .catch((err) => {
            console.log("Catch Block")
          if (err.response) {
            setAlert({ type: "error", message: err.response.data });
          } else {
            setAlert({ type: "error", message: CONNECTION_ERROR });
          }
          setShowErr(true)
          console.log(authUser)
          return
        });
        console.log(authUser)
    }

    useEffect(()=>{console.clear()},[])

    return (
        <div className="home">
           
            {/* if the user is already authenticated then redirect to execute page with out login page again. */}
            {
                authUser && authUser.authenticated &&
                <Redirect to={{pathname:'/execute'}} />
            }

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
                rules={[{ required: true, message: 'Please input your username!' }]}
                
            >
                <Input value={user.username} onChange={e=> {setUser({...user,username:e.target.value})
                setData({...data,username:e.target.value})}
            }/>
            </Form.Item>

            <Form.Item
                label="Password"
                name="password"
                rules={[{ required: true, message: 'Please input your password!' }]}
            >
                <Input.Password value={user.pass} onChange={e=>{setUser({...user,pass:e.target.value})
                setData({...data,password:bcrypt.hashSync(e.target.value, config.salt)})}
            }/>
            </Form.Item>

            <Form.Item {...tailLayout} name="remember" valuePropName="checked">
                <Checkbox>Remember me</Checkbox>
            </Form.Item>

            <Form.Item {...tailLayout}>
                <Button htmlType="submit" onClick={login} icon={<ArrowRightOutlined />}>Login</Button>
            </Form.Item>
        </Form>
        </div>
    )
}

export default Home