async function getRequest(url, data, method='GET'){
	const result = await fetch(url, {
       	mode: 'cors',
		headers:{
		      'Accept': 'application/json',
		      'Content-type': 'application/json',
		},
		method,
		body: data ? JSON.stringify(data): undefined,});
	return await result.json();
	
}

export {getRequest}
