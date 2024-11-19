import React, { useEffect, useState } from "react";
import { Table, Alert, Divider, Button } from "antd";
import { CheckCircleTwoTone, CloseCircleTwoTone } from "@ant-design/icons";
import { PageHeader } from '@ant-design/pro-layout';
import axios from "axios";
import HeaderCardField from "./HeaderCardField";
import { useHistory } from "react-router-dom";
import { FileExcelFilled } from "@ant-design/icons";
import ReactExport from "react-data-export";

import "../styles/claim-validation.css";

const endpoint = process.env.REACT_APP_API_URL;
const ExcelFile = ReactExport.ExcelFile;
const ExcelSheet = ReactExport.ExcelFile.ExcelSheet;
const ExcelColumn = ReactExport.ExcelFile.ExcelColumn;

const columns = [
  { title: "Claim #", dataIndex: "claimId", key: "claimId" },
  { title: "Condition Name", dataIndex: "query", key: "query" },
  { title: "Expected", dataIndex: "expected", key: "expected" },
  { title: "Actual", dataIndex: "actual", key: "actual" },
  {
    title: "Result",
    dataIndex: "result",
    key: "result",
    render: (result) =>
      result === "pass" ? (
        <CheckCircleTwoTone twoToneColor="#52c41a" />
      ) : (
        <CloseCircleTwoTone twoToneColor="#eb2f96" />
      ),
    filters: [
      {
        text: 'Fail',
        value: 'fail',
      },
      {
        text: 'Pass',
        value: 'pass',
      },
    ],
    onFilter: (value, record) => record.result.indexOf(value) === 0,
  },
];

function ClaimAccuracyResult(props) {
  const [result, setResult] = useState([]);
  const [alert, setAlert] = useState("");
  const [claimcomma, setclaimcomma] = useState([]);

  const id = props && props.match.params.id;
  const history = useHistory();

  useEffect(() => {
    if (!id) {
      return;
    }

    axios
      .get(`${endpoint}/claimaccuracyvalidator/claims/${id}`)
      .then((res) => {
        setResult(res.data);
      })
      .catch((err) => {
        if (err.response) {
          setAlert(err.response.data);
        } else {
          setAlert("Error connecting api server, please contact administrator");
        }
      });

  }, [id]);

  useEffect(() => {
    if (!result.claimIds) {
      return;
    }else{
      setclaimcomma(result.claimIds.join(" , "));
    }
  }, [result]);

  return (
    result && (
      <div className="claim-validation-container">
        {alert && (
          <Alert type="error" message={alert} style={{ marginBottom: 30 }} />
        )}
        <PageHeader
          title="View Claim Validation Results"
          style={{ alignSelf: "flex-start" }}
          onBack={() => {
            history.push({ pathname: "/tools/claimaccuracyvalidator" });
          }}
        >
          <div className="validation-header">
            <HeaderCardField
              title="Validation #"
              description={result.id}
            />
            <HeaderCardField title="Test Env" description={result.env} />
            <HeaderCardField title="User" description={result.username} />
            <HeaderCardField title="Claims" description={claimcomma} />
            <label htmlFor="ExcelDownload" style={{ marginRight: 10 }}>
              Download in Excel
            </label>

            <ExcelFile element={<Button
              name="ExcelDownload"
              icon={<FileExcelFilled />}
              type="primary"
            ></Button>}
            filename = "ExcelExport"
            fileExtenstion = "xlsx"
            >
              <ExcelSheet data={result.result} name="Results">
                <ExcelColumn label="Claim #" value="claimId" />
                <ExcelColumn label="Condition Name" value="query" />
                <ExcelColumn label="Expected" value="expected" />
                <ExcelColumn label="Actual" value="actual" />
                <ExcelColumn label="Result" value="result" />
              </ExcelSheet>
            </ExcelFile>

          </div>

        </PageHeader>

        <Divider />

        {result.result && (
          <Table
            columns={columns}
            dataSource={result.result.map((d) => ({ ...d, key: d.id }))}
            size="small"
          />
        )}
      </div>
    )
  );
}

export default ClaimAccuracyResult;
