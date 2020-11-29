const UploadImage = artifacts.require("UploadImage");

module.exports = function(deployer) {
  deployer.deploy(UploadImage);
};
