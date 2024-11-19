import React, { useEffect, useState } from "react";
import axios from "axios";
import { Table } from "antd";
import "../styles/side-nav.css";

const endpoint = process.env.REACT_APP_API_URL;

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 100
  },
  {
    title: 'Categories',
    dataIndex: 'categories',
  },
  {
    title: 'FilterbyPercentage',
    dataIndex: 'filterbypercentage',
  },
  {
    title: 'Count',
    dataIndex: 'count',
  },
  
];

const onChange = (pagination, filters, sorter, extra) => {
  console.log("params", pagination, filters, sorter, extra);
};

function PlaceofService() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${endpoint}/config/placeofservice/getallservices`);
      const processedData = response.data.map((item, index) => ({
        ...item,
        sno: `Claim ${index + 1}`, // Sequential S.no
      }));
      setData(processedData);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching data:", error);
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="input" style={{ marginLeft: 10, width: '100%' }}>
        <br />
        <Table
          columns={columns}
          dataSource={data}
          onChange={onChange}
          //scroll={{ x: 800 }}
          //pagination={{ pageSize: 100 }}
          loading={loading}
        />
      </div>
    </div>
  );
}

export default PlaceofService;
