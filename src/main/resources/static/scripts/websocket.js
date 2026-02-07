async function initWebsocket(messageCallback) {
    
    const ws = new WebSocket(`/api/socket`);

    ws.onmessage = (message) => {
        const json = JSON.parse(message.data);
        messageCallback(json);
    };

    const sendMessage = (json) => {
        const msg = JSON.stringify(json);
        ws.send(msg);
    };

    return new Promise((resolve, reject) => {
        if (ws.readyState == ws.OPEN) {
            resolve(sendMessage);
            return;
        }

        ws.onopen = () => {
            resolve(sendMessage);
        };

        ws.onerror = (err) => {
            reject(err);
        };
    });

}

export {initWebsocket};