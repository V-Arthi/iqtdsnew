import React from 'react'
import HeaderCardField from './HeaderCardField'
import {Layout} from 'antd'
const{Header}=Layout
function HeaderSection({type,entity}) {
    if(type==="run"){
        return(
            
            <Header className="run__header header-section">
             
                    <HeaderCardField title="Run #" description={entity.id} />
                    
                    <HeaderCardField title="Env" description={entity.env} />
                    
                    <HeaderCardField title="test #" description={entity.testCondition.testId} />
                    
                    <HeaderCardField title="test name" description={entity.testCondition.testName} />
                    
                    <HeaderCardField title="input type" description={entity.testCondition.claimInputType} />
                    
                    <HeaderCardField title="claim type" description={entity.testCondition.claimType} />
                    
                    <HeaderCardField title="status" description={entity.status} />
                    
                    <HeaderCardField title="creation date" description={entity.createdOn} />
              
            </Header>
            
        )
    }else if(type==="job"){
        return(
            <Header className="job__header header-section">
                <HeaderCardField title="Job #" description={entity.id} />
                <HeaderCardField title="Status" description={entity.status} />
                <HeaderCardField title="Creation Date" description={entity.createdOn} />
            </Header>
        )
    }else{
        return(<></>)
    }
}

export default HeaderSection
