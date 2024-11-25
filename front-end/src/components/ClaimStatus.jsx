import React, { useEffect, useState } from "react";
import axios from "axios";
import { Table } from "antd";
import "../styles/side-nav.css";

const endpoint = process.env.REACT_APP_API_URL;

const columns = [
  {
    title: 'S.no',
    dataIndex: 'sno',
    width: 100
  },
  {
    title: 'Claim Type',
    dataIndex: 'claimtype',
  },
  {
    title: 'Claim ID',
    dataIndex: 'claimid',
  },
  {
    title: 'Member ID',
    dataIndex: 'memberid',
  },
  {
    title: 'Provider ID',
    dataIndex: 'providerid',
  },
  {
    title: 'Date of Service',
    dataIndex: 'dateofservice',
  },
  {
    title: 'Charged Amount',
    dataIndex: 'chargedamount',
  },
  {
    title: 'Allowed Amount',
    dataIndex: 'allowedamount',
  },
  {
    title: 'Paid Amount',
    dataIndex: 'paidamount',
  },
  {
    title: 'Claim Status',
    dataIndex: 'claimstatus',
  },
];

const onChange = (pagination, filters, sorter, extra) => {
  console.log("params", pagination, filters, sorter, extra);
};

function ClaimStatus() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${endpoint}/config/claimstatus/getallclaims`);
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

export default ClaimStatus;
