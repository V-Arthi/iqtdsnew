import React, { useEffect, useState } from "react";
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
  Select
} from "antd";
import { SearchOutlined, QuestionCircleFilled } from "@ant-design/icons";
const { TextArea } = Input;
const { Text } = Typography;

const config = require("../config.json");
const endpoint = process.env.REACT_APP_API_URL;
const envs = ["", ...config.environments];
const apiUrl = `${endpoint}/validation/claims`;

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
  ClaimNotFound: "danger",
};

const columns = [
  {
    title: "ID",
    dataIndex: "id",
    key: "id",
    render: (id) => (
      <Link to={{ pathname: `/validate/claims/${id}` }}>{id}</Link>
    ),
    ...getColumnSearchProps("id"),
  },
  {
    title: "Test ID",
    dataIndex: "testId",
    key: "testId",
    ...getColumnSearchProps("testId"),
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
];

const emptyAlert = { type: "info", message: "" };

const CONNECTION_ERROR =
  "Error in connecting to api server, Please contact administrator";

function CVQueueAndForm(props) {
  const testID = (props.location.state && props.location.state.testId) || "";
  const env = (props.location.state && props.location.state.env) || "";
  const claimID = (props.location.state && props.location.state.claimID) || "";

  const [queue, setQueue] = useState([]);
  const [alert, setAlert] = useState(emptyAlert);
  const [tests, setTests] = useState([]);
  const [data, setData] = useState({ testId: "", claimIds: "", env: "" });

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

  function loadTest() {
    axios
      .get(`${endpoint}/conditions-lite`)
      .then((res) => {
        setTests(res.data);
        setData((prevData) => ({ ...prevData, testId: res.data[0].testId }));
      })
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
    loadTest();

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
      `direct request- test:${testID} | env:${env} | claim:${claimID}`
    );
    if (testID && env && claimID) {
      submitRequest({
        testId: parseInt(testID),
        claimIds: claimID.split(","),
        env,
      });
    }
  }, [testID, env, claimID]);

  const requestNew = (e) => {
    e.preventDefault();

    data["testId"] = parseInt(data.testId);
    data["claimIds"] = data.claimIds.split(",");

    if (!data.testId || !data.env || !data.claimIds) {
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
    setData({ testId: tests[0].testId, env: "", claimIds: "" });
  };

  const handleChange = name => value => {
    const data_clone = { ...data };
    data_clone[name] = value;
    setData(data_clone);
    console.log(data)
  };

  const handleTextChange = (e) => {
    const data_clone = { ...data };
    data_clone[e.target.name] = e.target.value;
    setData(data_clone);
    console.log(data)
  };

  return (
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
      {!(testID && env && claimID) && (
        <div className="claim-validation-request-form-container">
          <form
            onSubmit={(e) => requestNew(e)}
            onReset={resetForm}
            autoComplete="off"
          >
            <fieldset>
              <label htmlFor="test">Test Condition</label>
              <Select
                showSearch
                name="testId"
                id="testId"
                value={data.testId}
                popupMatchSelectWidth={false}
                onChange={handleChange("testId")}
                style={{width:400}}
                filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
              >
                {tests.map((test) => (
                  <option
                    key={test.testId}
                    value={test.testId}
                    label={`${test.testId} - ${test.testName}`}
                  >{`${test.testId}-${test.testName}`}</option>
                ))}
              </Select>

            </fieldset>
            <fieldset>
              <label htmlFor="env">Test Environment</label>
              <Select
                name="env"
                id="env"
                style={{width:90}}
                value={data.env}
                onChange={handleChange("env")}
              >
                {envs.map((env) => (
                  <option value={env} label={env}>
                    {env}
                  </option>
                ))}
              </Select>
            </fieldset>
            <fieldset>
              <label>Claim IDs</label>
              <TextArea
                name="claimIds"
                value={data.claimIds}
                onChange={handleTextChange}
                rows={2}
                placeholder="claimid1,claimid2..."
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
                <li>Select a test condition to validate a claim against</li>
                <li>Select test environment where the claim orginates from</li>
                <li>Enter claimID(s) and click validate</li>
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
        title={() => "Claim Validation History"}
      />
    </div>
  );
}

export default CVQueueAndForm;
