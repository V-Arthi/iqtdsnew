import React from "react";
 
import { UploadOutlined } from "@ant-design/icons";
import {Table,Button } from "antd";
import "../styles/side-nav.css";

const columns = [
    {
      title: 'ClaimID',
      dataIndex: 'claimid',
      filters: []
    }  ,
 {
        filterMode: 'tree',
        filterSearch: true,
        onFilter: (value, record) => record.name.includes(value),
        width: '15%',
    },
    
      {
        title: 'SubscriberID/name',
        dataIndex: 'subscriberid/name',
      
      },
      {
        onFilter: (value, record) => record.address.startsWith(value),
        filterSearch: true,
        width: '15%',
      },

      {
        title: 'ProviderID/name',
        dataIndex: 'providerid/name',
       
      },
      {
        onFilter: (value, record) => record.address.startsWith(value),
        filterSearch: true,
        width: '15%',
      },
      {
        title: 'ChargedAmount',
        dataIndex: 'chargedamount',
        //filters: []
      }  ,
   {
          filterMode: 'tree',
          filterSearch: true,
          onFilter: (value, record) => record.name.includes(value),
          width: '15%',
      },
      {
        title: 'PaidAmount',
        dataIndex: 'paidamount',
        //filters: []
      }  ,
   {
          filterMode: 'tree',
          filterSearch: true,
          onFilter: (value, record) => record.name.includes(value),
          width: '15%',
      },
      {
        title: 'Status',
        dataIndex: 'status',
        //filters: []
      }  ,
   {
          filterMode: 'tree',
          filterSearch: true,
          onFilter: (value, record) => record.name.includes(value),
          width: '15%',
      },
    ];  

const onChange = (pagination, filters, sorter, extra) => {
        console.log('params', pagination, filters, sorter, extra);
      };

function ClaimDetails(){

    return(
        <div className="input" style={{ marginLeft: 10 }}>
      <h1 style={{ marginLeft: 450 }}>Claim Details</h1>
      <br />

      

      
      <Table columns={columns} onChange={onChange} />
      </div>
    )
}
export default ClaimDetails;