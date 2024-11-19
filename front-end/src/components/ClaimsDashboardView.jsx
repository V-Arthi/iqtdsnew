import React, { useState, useEffect } from "react";
import axios from "axios";

import { UploadOutlined } from "@ant-design/icons";

import "../styles/side-nav.css";

import { Button, Upload, Space, Select, Table } from "antd";
import {PlaceofService} from "./PlaceofService";
const endpoint = process.env.REACT_APP_API_URL;

function ClaimsDashboardView() {
  const [testData, setTestData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [placeofService, setPlaceofService] = useState([]);
  const [selectedRows, setSelectedRows] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [loading, setLoading] = useState(true);
  const selectCategories = [
    {
      value: "1",
      label: "Type of service",
    },
    {
      value: "2",
      label: "Place of Service",
    },
    {
      value: "3",
      label: "Procedure code",
    },
    {
      value: "4",
      label: "Revenue code",
    },
    {
      value: "5",
      label: "Diagnosis code",
    },
    {
      value: "6",
      label: "Provider Type",
    },
    {
      value: "7",
      label: "High Volume Claims",
    },
    {
      value: "8",
      label: "High Dollar claims",
    },
  ];

  useEffect(() => {

    fetchTestData();
    fetchPlaceofServiceData();
  }, []);

  const fetchTestData = async ()=> {
    setLoading(true);
    try{
      const response = await axios.get(`${endpoint}/config/placeofservice/getallservices`);
      setTestData(response.data);
      setFilteredData(response.data);
      setLoading(false);
    }catch(error){
      console.error("error fetching test data",error);
      setLoading(false);
    }
  };
   
  
  const fetchPlaceofServiceData = async () =>{
    try{
      const response = await axios.get(`${endpoint}/config/placeofservice/getallservices`);
      const processedData = response.data.map((item,index) => ({
        item,
        sno: `Claim ${index +1}`,
      }));
      setPlaceofService(processedData);
    }catch (error) {
      console.error("error fetching",error);
    }
  };
  
  const handleSearch = () =>{
    if (selectedCategory === "2"){
        setFilteredData(placeofService);
    }else{
      setFilteredData(testData);
    }
  };

  const handleSelectCategory =(value) => {
    setSelectedCategory(value);
    if(value !== "2"){
      setPlaceofService("");
      setFilteredData(testData);
    }
  }

  console.log(testData);

  const handlefilter = (value, record) => {
    const percentage = record.percentage;
    switch (value) {
      case "0-25":
        return percentage >= 0 && percentage <= 25;
      case "26-50":
        return percentage >= 26 && percentage <= 50;
      case "51-75":
        return percentage >= 51 && percentage <= 75;
      case "76-100":
        return percentage >= 76 && percentage <= 100;
      default:
        return true;
    }
  };

  const columns = [
    { title: "ID", dataIndex: "id", key: "id", width: 180 },
    { title: "Categories", dataIndex: "procCode", key: "procCode" , width:180 },
    {
      title: "FilterBy Percentage",
      dataIndex: "percentage",
      key: "percentage",
      width: 150,
      filters: [
        {
          text: "0-25",
          value: "0-25",
        },
        {
          text: "26-50",
          value: "26-50",
        },
        {
          text: "51-75",
          value: "51-75",
        },
        {
          text: "76-100",
          value: "76-100",
        },
      ],
      onFilter: handlefilter,
      render: (text) => `${text}%`,
    },

    { title: "Count", dataIndex: "count", key: "count", width:180 },
  ];

  const handleSelectRow = (newSelectedRowKeys) => {
    setSelectedRows(newSelectedRowKeys);
  };
  const rowSelection = {
    selectedRowKeys: selectedRows,
    onChange: handleSelectRow,
  };
  const hasSelected = selectedRows.length > 0;

  return (
    <div className="input" style={{ marginLeft: 10 }}>
      <h1 style={{ marginLeft: 450 }}>Dashboard View</h1>
      <br />
      <Space>
        <Upload className="file-upload">
          <label> Upload Claims File : </label>
          <Button icon={<UploadOutlined />}>Click to Upload</Button>
        </Upload>
      </Space>
      <br />
      <br />
      <Space>
        <label>Select Categories:</label>
        <Select
          showSearch
          label="Select Categories"
          style={{
            width: 180,
          }}
          placeholder="Select Categories"
          optionFilterProp="children"
          filterSort={(optionA, optionB) =>
            (optionA?.label ?? "")
              .toLowerCase()
              .localeCompare((optionB?.label ?? "").toLowerCase())
          }
          options={selectCategories}
        />
        <Button onClick={handleSearch}>Search</Button>

      </Space>
      <br />
      <Button
        type="primary"
        disabled={!hasSelected}
        htmlType="submit"
        className="submit-btn"
        style={{ marginLeft: 1000, width: 120 }}
      >
        Add
      </Button>
      <br />
      <br />
      <Table
        columns={columns}
        rowKey="id"
        rowSelection={rowSelection}
        dataSource={testData.map((items) => ({
          id: items.id,
          procCode: items.procCode,
          count: items.count,
          percentage: items.percentage,
        }))}
      ></Table>
      ;
    </div>
  );
}

export default ClaimsDashboardView;
