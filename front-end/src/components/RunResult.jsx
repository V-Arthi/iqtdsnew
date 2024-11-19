import { Button, Table, Empty } from 'antd'
import React, { useEffect, useState } from 'react'
import {useHistory} from 'react-router-dom'


function RunResult({env,result,title ,selectable,askedSize,id}) {
    const[picked,setPicked] = useState([])
    
    const history = useHistory()
    
    const rowSelection = {
      onChange: (selectedRowKeys,selectedRows) => {
        setPicked(selectedRows.map(d=>d.ID).filter((item,index,ar)=>ar.indexOf(item)===index))
        //console.log(selectedRows)
      }
    };

    useEffect(()=>{console.clear()},[])

    

    const hasSelected = picked.length>0

    const extractEDI = () =>{
        
        const data = picked.map(i=>({id:i,env:env,runId:id}))
        
        history.push({
          pathname:'/result/extract',
          state:{data,back:history.location.pathname}
        })
    }

    return (
        result ?
        <div className="run_result_table">
            
            <Table
                rowSelection={selectable && {
                    type:'checkbox',
                    ...rowSelection,
                }}
                title={()=>
                          <div className="result_table_header">
                            <div className="result_table_title"><span>{title}</span></div>
                            { selectable &&
                              <div className="result_table_actions">
                                <span style={{marginRight:10}}>{hasSelected && `Selected ${picked.length} distinct item(s)`}</span>
                                <Button disabled = {!hasSelected} type="primary" onClick={extractEDI}>Extract EDI</Button>
                              </div>
                            }
                          </div>
                      } 
                small 
                columns = {Object.keys(result[0].data).map(c=>({title:c,dataIndex:c,width:75}))} 
                dataSource={result.map(r=>({...r.data,key:r.id}))} 
                scroll={{x:600}} 
                footer={()=>askedSize && `Requested : ${askedSize} | Found : ${result.length}`}
                
            />
        </div> 
        :
        <Empty description={<span>{`No data found for ${title}`}</span>} />
        
        
    )
}

export default RunResult