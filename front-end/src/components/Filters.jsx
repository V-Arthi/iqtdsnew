import { Button } from 'antd'
import React from 'react'
import {DeleteOutlined,MoreOutlined} from '@ant-design/icons'

function Filters({name,filters,removeFn,fillers}) {

    return (
        
        <ul className="filters_list" name={name}>
            {
                fillers && fillers.length >0 &&
                fillers.map((filler,index)=><li key={index} className="filter_item filler">{filler}</li>)
            }
            {
                filters.length >0 &&
                filters.map((filter,index)=>(
                    <li key={index} className="filter_item">
                        <div className="filter_item_value">
                            <span className="field">{filter.field}</span>
                            <span className="operator">{filter.operator}</span>
                            <span className="value">{filter.value}</span>
                        </div>
                        <MoreOutlined style={{color:'crimson'}}/>
                        <div className="filter_item_action">
                            <Button icon={<DeleteOutlined />} type="link" className="btn_icon" onClick={e=>removeFn(filter,name)} danger/>
                        </div>
                    </li>
                ))
            }
            {
                filters.length<1 &&
                <div className="filter_list_empty">
                    <p>No Specific Filters Defined</p>
                </div>
            }
            
        </ul>
        
    )
}

export default Filters