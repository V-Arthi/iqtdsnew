import React from 'react'

import {CloseOutlined} from '@ant-design/icons'
import {Button} from 'antd'

function Override({override,removeFn}) {
    return (
        <li className="override" >
            <span>{override.fieldName}</span>
            { override.txnNumber && <span style={{color:'#888'}}>@</span> }
            <span>{override.txnNumber}</span>
            <span style={{color:'#888'}}>=</span>
            <span>{override.fieldValue}</span>
            <Button icon={<CloseOutlined />} type="link" onClick={removeFn} style={{color:'crimson',marginLeft:10}}/>
        </li>
    )
}

export default Override
