import React, { useContext,useState } from 'react'
import {Link} from 'react-router-dom'
import {Avatar,Drawer,Button, Divider} from 'antd'
import {UserContext} from '../Contexts/UserContext'
import {UserOutlined} from '@ant-design/icons'

import '../styles/user.css'

import Text from 'antd/lib/typography/Text'

function User() {

    const {authUser,setAuthUser} = useContext(UserContext)

    const [open,setOpen] = useState(false)

    const showDrawer = ()=>{
        setOpen(true)
    }

    const closeDrawer = () =>{
        setOpen(false)
    }

    const logoutUser = () => {
		setOpen(false)
        setAuthUser({...authUser,authenticated:false})
        //localStorage.removeItem("user")
    }

    return (
        <>
            {
                authUser.authenticated ?
               
                <div className="user-menu">
                    <Button className="user-button" icon={<UserOutlined />} onClick={showDrawer} type="link">{authUser.name}</Button>
                    <Drawer placement="right" closable onClose={closeDrawer} open={open}>
                        <div className="user-section">
                            <Avatar size="large" icon={<UserOutlined />} style={{margin:"20px auto"}}/>
                            <Text className="user-name">{authUser.name}</Text>
                            <Text className="user-role">{authUser.role}</Text>
                        </div>
                        <Divider />
                        <Button disabled type="link">Change Password</Button>
                        <Button className="btn-logout" type="link" onClick={logoutUser}>Logout</Button>
                        <Divider />
                    </Drawer>

                </div>
                :
                <Link to={{pathname:'/home'}} style={{marginRight:50}}>Login</Link>
            }
        </>
    )
}

export default User
