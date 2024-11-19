import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import { Table, Button, Space, Input, Result, message, Tag } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import axios from "axios";
import { UserContext } from "../Contexts/UserContext";

const endpoint = process.env.REACT_APP_API_URL;

const getColumnSearchProps = (dataIndex) => ({
  filterDropdown: ({
    setSelectedKeys,
    selectedKeys,
    confirm,
    clearFilters,
    searchInput,
  }) => (
    <div style={{ padding: 8 }}>
      <Input
        ref={(node) => (searchInput = node)}
        placeholder={`Search ${dataIndex}`}
        value={selectedKeys[0]}
        onChange={(e) =>
          setSelectedKeys(e.target.value ? [e.target.value] : [])
        }
        onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
        style={{ width: 188, marginBottom: 8, display: "block" }}
      />
      <Space>
        <Button
          type="primary"
          onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
          icon={<SearchOutlined />}
          size="small"
          style={{ width: 90 }}
        >
          Search
        </Button>
        <Button
          onClick={() => handleReset(clearFilters)}
          size="small"
          style={{ width: 90 }}
        >
          Reset
        </Button>
      </Space>
    </div>
  ),
  filterIcon: (filtered) => (
    <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
  ),
  onFilter: (value, record) =>
    record[dataIndex]
      ? record[dataIndex].toString().toLowerCase().includes(value.toLowerCase())
      : "",
});

const jobColumns = [
  {
    title: "Job ID",
    dataIndex: "id",
    key: "id",
    ...getColumnSearchProps("id"),
    render: (id) => (
      <Link to={{ pathname: `/jobs/view/${id}` }} style={{ display: "block" }}>
        {id}
      </Link>
    ),
  },
  {
    title: "Environment",
    dataIndex: "env",
    key: "env",
    ...getColumnSearchProps("env"),
  },
  {
    title: "Test ID",
    dataIndex: "testID",
    key: "testID",
    ...getColumnSearchProps("testID"),
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    ...getColumnSearchProps("status"),
    render: (status) => (
      <Tag
        color={
          status.toLowerCase() === "completed"
            ? "success"
            : status.toLowerCase() === "in progress"
              ? "processing"
              : ""
        }
      >
        {
          status.toUpperCase().split('\n').map(function (item, idx) {
            return (
              <span key={idx}>
                {item}
                <br />
              </span>
            )
          })
        }
      </Tag>
    ),
  },
  {
    title: "Created On",
    dataIndex: "createdOn",
    key: "createdOn",
    ...getColumnSearchProps("createdOn"),
  },
];

const handleSearch = (selectedKeys, confirm, dataIndex) => {
  confirm();
};

const handleReset = (clearFilters) => {
  clearFilters();
};

function JobMonitor() {
  const { authUser } = useContext(UserContext);

  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    function loadJobs() {
      console.clear();
      axios
        .get(`${endpoint}/monitor/jobs-lite`)
        .then((res) => {
          //console.log('jobs',res.data)
          setJobs(res.data);
          setLoading(false);
        })
        .catch((err) => message.error(err.message, 30));
    }

    if (authUser.authenticated) {
      loadJobs();
    }
    const interval = setInterval(() => loadJobs(), 60 * 1000);

    return () => {
      clearInterval(interval);
    };
  }, [authUser.authenticated]);

  return (
    <div className="job__monitor" style={{ padding: "20px 30px" }}>
      {authUser.authenticated ? (
        <Table
          style={{ margin: "20px auto", width: "75%" }}
          loading={loading}
          columns={jobColumns}
          dataSource={jobs.map((job) => ({ ...job, key: job.id }))}
        />
      ) : (
        <Result
          status="403"
          title="UnAuthorized"
          subTitle="Please login to view this page"
        />
      )}
    </div>
  );
}

export default JobMonitor;
