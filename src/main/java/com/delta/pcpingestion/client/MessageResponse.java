package com.delta.pcpingestion.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
	private String message;
}
