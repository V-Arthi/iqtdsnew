import React,{useState} from 'react'
import {Input, message, Tag} from 'antd'
import {PlusOutlined,GroupOutlined} from '@ant-design/icons'

function TestTags({test,setTest}) {
    const[tag,setTag]=useState('')

    const addTag=e=>{
        const value=e.target.value
        if(!value) return
        const test_copy = {...test}
        const exisitng_tags = test_copy['tags']
        const existing_as_arr = exisitng_tags ? exisitng_tags.split(",") :[]

        if(existing_as_arr.indexOf(value)>=0){
            message.info(`Tag '${value}' already exists`)
            return
        }

        const newTags = [...existing_as_arr,value].join() 
        test_copy['tags']=newTags
        setTest(test_copy)
        setTag('')
    }

    const handleChange =e=>{
        setTag(e.target.value)
    }
    const removeTag = removedTag =>{
        const test_copy = {...test}
        const exisitng_tags = test_copy.tags.split(",")
        const afterRemoval = exisitng_tags.filter(t=>t!==removedTag).join()
        test_copy['tags']=afterRemoval
        setTest(test_copy)
    }
    return (
        <div className="tags_section">
            <Tag color="#03506f" icon={<GroupOutlined/>} style={{marginRight:10}}>Groups</Tag>
            
            {
                test && test.tags &&
                <div className="tags">
                    
                    {
                        test.tags.split(",").map((t,index)=><Tag key={index} color="magenta" visible className="test_tag" closable onClose={e=>removeTag(t)}>{t}</Tag>)    
                    }
                </div>
            }

            <Input prefix={<PlusOutlined style={{color:'#888'}}/>} placeholder="Add Group" className="new_tag_input" allowClear value={tag} onChange={handleChange} onPressEnter={addTag} bordered={false} size="small" />
            
        </div>
    )
}

export default TestTags
