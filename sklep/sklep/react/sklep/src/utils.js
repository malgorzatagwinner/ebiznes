async function getRequest(url, data, method='GET'){
	const result = await fetch(url, {
       	mode: 'cors',
		headers:{
		      'Accept': 'application/json',
		      'Content-type': 'application/json',
		},
		method,
		body: data ? JSON.stringify(data): undefined,});
	const json = await result.json();
	return json
}

export {getRequest}
