import React,{useContext} from 'react'
import {Redirect} from 'react-router'
import {UserContext} from '../Contexts/UserContext'
import {TestsGrid} from '.'

function Execute() {
    const {authUser} =useContext(UserContext)
    return (
        <>{
            authUser.authenticated  && authUser.role !=='guest' ?
            <TestsGrid runMode />
            :
            <Redirect to={{pathname:'/home'}} />
          }
        </>
    )
}

export default Execute
