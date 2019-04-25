const AWS = require("aws-sdk");
const rekognition = new AWS.Rekognition();

exports.handler = async event => {
  var file = event.fileName;
  var imgBase64 = event.data64;

  try {
    const body = await Promise.all(Analize(imgBase64));
    const bodyImg = body[0];
    const bodyText = body[1];

    var resp = {
      fileName: file,
      Tags: bodyImg.Labels,
      Text: bodyText.TextDetections
    };

    return resp;
  } catch (err) {
    var resp = {
      fileName: file,
      Error: {
        Error: err.message,
        Description: err.stack
      }
    };
    return resp;
  }
};

const Analize = imgData64 => {
  var params = {
    Image: {
      Bytes: new Buffer(imgData64, "base64")
    },
    MaxLabels: 10,
    MinConfidence: 75.0
  };

  var promImage = rekognition.detectLabels(params).promise();

  var params2 = {
    Image: {
      Bytes: new Buffer(imgData64, "base64")
    }
  };

  var promText = rekognition.detectText(params2).promise();
  return [promImage, promText];
};
