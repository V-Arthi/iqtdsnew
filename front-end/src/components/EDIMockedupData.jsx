import React from 'react'
import {Divider,Button,Collapse} from 'antd'
import Paragraph from 'antd/lib/typography/Paragraph'
import Text from 'antd/lib/typography/Text'

const {Panel} = Collapse

const createFile = (fileName,data) =>{
    const f = new Blob([data],{
        type:'text/plain'
    })
    const x12File= window.URL.createObjectURL(f)
    const el = document.createElement("a")
    el.setAttribute("id","download_link")
    el.href = x12File
    el.download = fileName
    el.click()
    el.remove()
}
function EDIMockedupData({dataEDI}) {
    return (
        <>
            <Divider />
            <Text>{dataEDI.filename}</Text>
            <span><Button type="link" onClick={e=>createFile(dataEDI.filename,dataEDI.edi)}>Download as File</Button></span>
            <Paragraph copyable code>
                {dataEDI.edi}
            </Paragraph>
            
            <Collapse accordion>
                    <Panel header="Mockup Logs" key={`${dataEDI.fileName}_log`} className="logs_box">
                        <div className="logs" style={{backgroundColor:'#000',color:'#fff',fontStyle:'italic',padding:'5px',fontSize:'12px'}}>
                            {
                                dataEDI.logs.split('\n').map((line,index)=>(
                                <span key={`edi_line_${index}`}>{line}<br /></span>
                            ))}
                        </div>
                </Panel>
            </Collapse>
        </>
    )
}

export default EDIMockedupData
