import React,{useContext} from 'react'
import {UserContext} from '../Contexts/UserContext'
import {TestsGrid} from '.'
import {Result} from 'antd'
 

function Manage() {

    const {authUser} = useContext(UserContext)
   
    return (
        <>
        { 
            authUser.authenticated  && ["administrator","maintainer"].indexOf(authUser.role) >= 0 ?
            <TestsGrid />
            :
            <Result 
                status="403" 
                title="UnAuthorized"
                subTitle="Please login as admin or manager to view this page" 
            />
        }
        </>
    )
}

export default Manage
