import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import {
  Table,
  Tag,
  Input,
  Button,
  Space,
  Typography,
  Alert,
  Divider,
  Popover,
} from "antd";
import { SearchOutlined, QuestionCircleFilled } from "@ant-design/icons";
import { UserContext } from "../Contexts/UserContext";

const { Text } = Typography;

const config = require("../config.json");
const endpoint = process.env.REACT_APP_API_URL;
const envs = ["", ...config.environments];
const apiUrl = `${endpoint}/claimaccuracyvalidator/claims`;

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

const handleSearch = (selectedKeys, confirm, dataIndex) => {
  confirm();
};

const handleReset = (clearFilters) => {
  clearFilters();
};

const statusDict = {
  Completed: "success",
  Validating: "warning",
  Error: "danger",
  Submitted: "secondary",
};

const columns = [
  {
    title: "ID",
    dataIndex: "id",
    key: "id",
    render: (id) => (
      <Link to={{ pathname: `/tools/claimaccuracyvalidator/${id}` }}>{id}</Link>
    ),
    ...getColumnSearchProps("id"),
  },
  {
    title: "Env",
    dataIndex: "env",
    key: "env",
    ...getColumnSearchProps("env"),
  },
  {
    title: "Claims",
    dataIndex: "claimIds",
    key: "claimIds",
    render: (claimIds) =>
      claimIds.map((c, index) => <Tag key={index}>{c}</Tag>),
    ...getColumnSearchProps("claimIds"),
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    render: (status) => <Text type={statusDict[status]}>{status}</Text>,
    ...getColumnSearchProps("status"),
  },
  { title: "Submitted On", dataIndex: "createdOn", key: "createdOn" },
  { title: "Username", dataIndex: "username", key: "username" },
];

const emptyAlert = { type: "info", message: "" };

const CONNECTION_ERROR =
  "Error in connecting to api server, Please contact administrator";

function ClaimAccuracyValidator(props) {
  const { authUser } = useContext(UserContext);
  const env = (props.location.state && props.location.state.env) || "";
  const claimID = (props.location.state && props.location.state.claimID) || "";

  const [queue, setQueue] = useState([]);
  const [alert, setAlert] = useState(emptyAlert);
  const [data, setData] = useState({ claimIds: "", env: "", username: authUser.name });

  function loadQ() {
    axios
      .get(apiUrl)
      .then((res) => setQueue(res.data.map((d) => ({ ...d, key: d.id }))))
      .catch((err) => {
        if (err.response) {
          setAlert({ type: "error", message: err.response.data });
        } else {
          setAlert({ type: "error", message: CONNECTION_ERROR });
        }
      });
  }

  useEffect(() => {
    loadQ();

    const interval = setInterval(loadQ, 10000);

    return () => {
      clearInterval(interval);
    };
  }, []);

  useEffect(() => {
    function submitRequest(data) {
      axios
        .post(apiUrl, data)
        .then((res) => {
          setAlert({ type: "success", message: res.data });
          loadQ();
        })
        .catch((err) => {
          if (err.response) {
            setAlert({ type: "error", message: err.response.data });
          } else {
            setAlert({ type: "error", message: CONNECTION_ERROR });
          }
        });
    }

    console.log(
      `direct request- env:${env} | claim:${claimID}`
    );
    if (env && claimID) {
      submitRequest({
        claimIds: claimID.split(","),
        env,
      });
    }
  }, [ env, claimID]);

  const requestNew = (e) => {
    e.preventDefault();

    data["claimIds"] = data.claimIds.split(",");

    if (!data.env || !data.claimIds) {
      setAlert({ type: "error", message: "All fields are mandatory" });
      return;
    }

    axios
      .post(apiUrl, data)
      .then((res) => {
        setAlert({ type: "success", message: res.data });
        loadQ();
        resetForm();
      })
      .catch((err) => {
        if (err.response) {
          setAlert({ type: "error", message: err.response.data });
        } else {
          setAlert({ type: "error", message: CONNECTION_ERROR });
        }
      });
  };

  const resetForm = () => {
    setData({ env: "", claimIds: "", username: authUser.name });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    const data_clone = { ...data };
    data_clone[name] = value;
    setData(data_clone);
  };

  return (
    authUser &&
    authUser.authenticated && (
    <div className="claim-validation-container">
      {alert.message && (
        <Alert
          type={alert.type}
          message={alert.message}
          showIcon
          closable
          onClose={() => setAlert({ type: "info", message: "" })}
          style={{ marginBottom: 30 }}
        />
      )}
      {!(env && claimID) && (
        <div className="claim-validation-request-form-container">
          <form
            onSubmit={(e) => requestNew(e)}
            onReset={resetForm}
            autoComplete="off"
          >
            <fieldset>
              <label htmlFor="env">Test Environment</label>
              <select
                name="env"
                id="env"
                value={data.env}
                onChange={(e) => handleChange(e)}
              >
                {envs.map((env, index) => (
                  <option key={index} value={env}>
                    {env}
                  </option>
                ))}
              </select>
            </fieldset>
            <fieldset>
              <label>Claim IDs</label>
              <textarea
                name="claimIds"
                value={data.claimIds}
                onChange={(e) => handleChange(e)}
                rows={2}
                cols={30}
                placeholder="claimid1,claimid2..."
                style={{ resize: "none" }}
                required
              />
            </fieldset>
            <Button type="primary" htmlType="submit" className="submit-btn">
              Validate
            </Button>
            <Button type="ghost" htmlType="reset">
              Clear
            </Button>
          </form>
          <Popover
            content={
              <ul>
                <li>Select test environment where the claim orginates from</li>
                <li>Enter claimID(s) and click validate to validate the Accuracy!</li>
              </ul>
            }
            title="Instructions"
          >
            <Button
              icon={<QuestionCircleFilled />}
              type="link"
              style={{ marginLeft: 30 }}
            />
          </Popover>
        </div>
      )}
      <Divider />

      <Table
        columns={columns}
        dataSource={queue}
        size="small"
        bordered
        title={() => "Claim Accuracy Validation History"}
      />
    </div>
  )
  )
}

export default ClaimAccuracyValidator;
