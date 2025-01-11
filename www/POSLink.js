var exec = require('cordova/exec');

var PAXPos = {
    /**
     * process Payment request
     * @param {*} request 
     * @param {*} success 
     * @param {*} failure 
     */
    processPayment: function (request, success, failure) {
        exec(success, failure, "PAXPos", "processPayment", [request]);
    },
    /**
     * process Resport request
     * @param {*} request 
     * @param {*} success 
     * @param {*} failure 
     */
    processReport: function (request, success, failure) {
        exec(success, failure, "PAXPos", "processReport", [request]);
    },
    /**
     * process Manage request
     * @param {*} request 
     * @param {*} success 
     * @param {*} failure 
     */
    processManage: function (request, success, failure) {
        exec(success, failure, "PAXPos", "processManage", [request]);
    },
    /**
     * process Batch request
     * @param {*} request 
     * @param {*} success 
     * @param {*} failure 
     */
    processBatch: function (request, success, failure) {
        exec(success, failure, "PAXPos", "processBatch", [request]);
    },
    /**
     * open scan
     * @param {*} scannerType 
     * @param {*} success 
     * @param {*} failure 
     */
    openScan: function (scannerType, success, failure) {
        scannerType = scannerType || null;
        exec(success, failure, "PAXPos", "openScan", [scannerType]);
    },
    /**
     * close scan
     * @param {*} success 
     * @param {*} failure 
     */
    closeScan: function (success, failure) {
        exec(success, failure, "PAXPos", "closeScan", []);
    },
    /**
     * scan
     * @param {*} success 
     * @param {*} failure 
     */
    scan: function (success, failure) {
        exec(success, failure, "PAXPos", "scan", []);
    },
    /**
     * print
     * @param {*} data 
     * @param {*} success 
     * @param {*} failure 
     */
    print: function (data, success, failure) {
        var dataToWrite = data instanceof Uint8Array
            ? data.buffer
            : data;        
        var type = Object.prototype.toString.call(dataToWrite).slice(8, -1);
        if (type != 'ArrayBuffer') {
            throw new Error('PAXPos.print - data is not an Array Buffer! (Got: ' + type + ')');
        }        
        exec(success, failure, "PAXPos", "executePrint", [dataToWrite]);
    }
};

module.exports = PAXPos;