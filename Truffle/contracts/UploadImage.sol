// Specifies the version of Solidity, using semantic versioning.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma
pragma solidity ^0.5.10;

// Defines a contract named `HelloWorld`.
// A contract is a collection of functions and data (its state).
// Once deployed, a contract resides at a specific address on the Ethereum blockchain.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/structure-of-a-contract.html
contract UploadImage {

  struct Image {
    bytes32 hash;
    address uploaderAddress;
    string name;
  }

  Image[] images;

    // Declares a state variable `message` of type `string`.
    // State variables are variables whose values are permanently stored in contract storage.
    // The keyword `public` makes variables accessible from outside a contract
    // and creates a function that other contracts or clients can call to access the value.
    string public message;

    // Similar to many class-based object-oriented languages, a constructor is
    // a special function that is only executed upon contract creation.
    // Constructors are used to initialize the contract's data.
    // Learn more: https://solidity.readthedocs.io/en/v0.5.10/contracts.html#constructors
    constructor() public {
        // Accepts a string argument `initMessage` and sets the value
        // into the contract's `message` storage variable).
        message = "Test string";
    }

    //Adds an image hash to the list
    function addImage(bytes32 hash, string memory name) public
    {
      //Ignore duplicates.
      if (!isAlreadyUploaded(hash)) {
        Image memory image;
        image.uploaderAddress = msg.sender;
        image.hash = hash;
        image.name = name;
        images.push(image);
      }
      else {
        revert();
      }
    }

    // Function to return length
    // of dynamic array
    function getLength() public view returns (uint)
    {
      return images.length;
    }

    //Returns the number of images the client has uploaded
    function getNumberOfUploadedImages() public view returns(uint) {
      address addr = msg.sender;
      uint count = 0;
      uint i;
      for(i = 0; i < images.length; i++)
      {
        if(images[i].uploaderAddress == msg.sender)
        {
          count++;
        }
      }

      return count;
    }

    //Searches the list of hashed images and returns a list of the hashes
    //the client calling the function has uploaded
    function search() public view returns(bytes32[] memory)
    {
      address addr = msg.sender;
      uint length = getNumberOfUploadedImages();
      bytes32[] memory hashes = new bytes32[](length);

      uint i;
      uint j;
      for (i = 0; i < length; i++) {
        for (j = 0; j < images.length; j++) {
          if (images[j].uploaderAddress == addr) {
            hashes[i] = images[i].hash;
          }
        }
      }
      return hashes;
    }

    function getImageNameFromHash(bytes32 hash) public view returns (string memory) {
      uint length = getNumberOfUploadedImages();
      uint i;
      uint j;
      for (i = 0; i < length; i++) {
        for (j = 0; j < images.length; j++) {
          if (images[j].hash == hash) {
            return images[j].name;
          }
        }
      }
    }

    function isAlreadyUploaded(bytes32 hash) public view returns (bool) {
      uint length = getNumberOfUploadedImages();
      uint i;
      uint j;
      for (i = 0; i < length; i++) {
        for (j = 0; j < images.length; j++) {
          if (images[j].hash == hash) {
            return true;
          }
        }
      }

     return false;
    }
}
