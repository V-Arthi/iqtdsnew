import React from 'react'
import {} from 'react-router'
import {Result,Button} from 'antd'
import { Link } from 'react-router-dom'

function NotFound() {
    return (
        <Result 
                status="404" 
                title="404 Not Found" 
                subTitle="Sorry, the page you visited doesnot exist or removed"
                extra={
                    <Link to={{pathname:'/home'}}>
                        <Button type="primary">Go To Home</Button>
                    </Link>
                }
        />
    )
}

export default NotFound
