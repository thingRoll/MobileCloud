import React, { Component } from 'react';
import { AppRegistry, Text } from 'react-native';

export default class Download extends Component {
  render() {
    return (
      <Text>更新的模块！</Text>
    );
  }
}

// skip this line if using Create React Native App
AppRegistry.registerComponent('MobileCloud', () => Download);