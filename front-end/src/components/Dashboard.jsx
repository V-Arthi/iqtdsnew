import React, { useState, useEffect } from "react";
import axios from "axios";
import { Row, Col, Layout, message, Card, Spin } from "antd";

import "../styles/dashboard.css";

const { Content } = Layout;

const endpoint = process.env.REACT_APP_API_URL;

const getstat = () => {
  return axios.get(`${endpoint}/dashboard/snapshot`);
};

const CardStat = ({ title, value, icon }) => (
  <Col span={6} className="dashboard__column">
    <Card className="card_statistic">
      {icon && <div className="prefix">{icon}</div>}
      <div className="statistic-data">
        <span className="stats-value">{value}</span>
        <span className="stats-title">{title}</span>
      </div>
    </Card>
  </Col>
);

function Dashboard() {
  const [data, setData] = useState({
    conditions: 0,
    runs: 0,
    jobs: 0,
    ediFiles: 0,
    onlineClaims: 0,
    mappings: 0,
    extracts: 0,
  });
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    function loadData() {
      getstat()
        .then((res) => {
          setData(res.data);
          setLoading(false);
        })
        .catch((err) => {
          console.log(err);
          message.error("Error in retrieving dashboard data");
        });
    }
    loadData();
    const interval = setInterval(() => {
      loadData();
    }, 5000);

    return () => {
      clearInterval(interval);
    };
  }, []);

  return (
    <Content className="dashboard">
      <Spin tip="data loading... please wait..." spinning={loading} />
      <Row gutter={16} className="dashboard__row">
        <CardStat title="test conditions maintained" value={data.conditions} />
        <CardStat title="runs executed" value={data.runs} />
        <CardStat title="jobs handled" value={data.jobs} />
        <CardStat title="edi fields configured" value={data.mappings} />
      </Row>
      <Row gutter={16} className="dashboard__row">
        <CardStat title="edi files generated" value={data.ediFiles} />
        <CardStat title="online claims created" value={data.onlineClaims} />
        <CardStat title="edi data extracted" value={data.extracts} />
      </Row>
    </Content>
  );
}

export default Dashboard;
