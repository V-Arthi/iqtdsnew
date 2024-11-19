import React from 'react'
import {Link} from 'react-router-dom'

import {User} from '.'

import '../styles/navigation.css'
import logo from '../assets/logo.svg'


const nav = [
    {title:'execute',path:'/execute',roles:['administrator','maintainer','developer']},
    {title:'design',path:'/design',roles:['administrator','maintainer']},
    {title:'jobs',path:'/jobs',roles:['administrator','maintainer','developer','guest']}
]

function Navigation() {
    
    
    return (
        <nav className="navigation">
            <Link to={{pathname:"/home"}} className="toolName"><span className="tool-title">intelligent Quality Test Data Sourcing</span></Link>
            <div className="logo-svg">
                <img src={logo} alt="cognizant-logo" style={{height:20,width:'auto'}} />
            </div>
             <User />
        </nav>
    )
}

export default Navigation
