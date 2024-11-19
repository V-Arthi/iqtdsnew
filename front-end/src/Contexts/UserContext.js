import React,{useState,createContext, useEffect} from 'react'

export const UserContext = createContext()

const user = {name:'',role:'',authenticated:false}

export const UserProvider = (props) =>{

    const[authUser,setAuthUser] = useState(JSON.parse(localStorage.getItem('user'))||{...user})

    useEffect(()=>{
        localStorage.setItem('user',JSON.stringify(authUser))
    },[authUser])

    return(
        <UserContext.Provider value={{authUser,setAuthUser}} >
            {props.children}
        </UserContext.Provider>
    )
}